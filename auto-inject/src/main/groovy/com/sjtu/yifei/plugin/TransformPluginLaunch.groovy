package com.sjtu.yifei.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sjtu.yifei.asm.IClassVisitor
import com.sjtu.yifei.asm.InjectClassVisitor
import com.sjtu.yifei.utils.InjectInfo
import com.sjtu.yifei.utils.Log
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

/**
 *
 * 标准transform的格式，一般实现transform可以直接拷贝一份重命名即可
 *
 * 两处todo实现自己的字节码增强／优化操作
 */
class TransformPluginLaunch extends Transform implements Plugin<Project> {

    private static final String NAME = "TransformPluginLaunch"
    public static final String EXT_NAME = 'autoinject'

    Project project

    @Override
    void apply(Project project) {
        this.project = project
        def isApp = project.plugins.hasPlugin(AppPlugin)
        //only application module needs this plugin to generate register code
        if (isApp) {
            project.extensions.create(EXT_NAME, AutoInjectConfig)
            def android = project.extensions.getByType(AppExtension)
            android.registerTransform(this)
            project.afterEvaluate {
                AutoInjectConfig config = project.extensions.findByName(EXT_NAME) as AutoInjectConfig
                boolean isDebug = config.isDebug
                Log.make(project, isDebug)
            }
        }
    }

    @Override
    String getName() {
        return NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        Log.i(NAME, "========== Transform scan start ===========")
        InjectInfo.get().retEnv()
        def startTime = System.currentTimeMillis()

        //todo step1: 先扫描
        transformInvocation.inputs.each {
            TransformInput input ->
                input.jarInputs.each { JarInput jarInput ->
                    String destName = jarInput.name
                    // rename jar files
                    def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length() - 4)
                    }
                    // input file
                    File src = jarInput.file
                    // output file
                    File dest = transformInvocation.outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                    //todo 对jar进行扫
                    if (!excludeJar(src.getAbsolutePath())) {
                        if (src.getAbsolutePath().endsWith(".jar")) {
                            scanJar(src, dest)
                        }
                    }

                    FileUtils.copyFile(src, dest)

                }

                input.directoryInputs.each { DirectoryInput directoryInput ->
                    //处理完输入文件之后，要把输出给下一个任务
                    File dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    String root = directoryInput.file.absolutePath

                    //todo 对目录进行扫描
                    if (directoryInput.file.isDirectory()) {
                        directoryInput.file.eachFileRecurse { File file ->
                            //注意 这里需要生成最终插入的目标class path
                            def path = file.absolutePath.replace(root, '')
                            if(file.isFile()){
                                File destClass = new File(dest.absolutePath + path)
                                scanFile(file, destClass)
                            }
                        }
                    }
                    FileUtils.copyDirectory(directoryInput.file, dest)
                }
        }
        def cost = (System.currentTimeMillis() - startTime) / 1000
        startTime = System.currentTimeMillis()
        Log.i(NAME, "========== Transform scan end cost $cost secs and start inserting ===========")
        //todo step2: ...完成代码注入
        if (InjectInfo.get().injectToClass != null) {

            if (InjectInfo.get().injectToClass.name.endsWith(".jar")) {//插入的类在jar
                Log.e(NAME, "Inserting code to jar >> " + InjectInfo.get().injectToClass.absolutePath)
                File jarFile = InjectInfo.get().injectToClass
                File optJar = new File(jarFile.parent, jarFile.name + ".opt")
                if (optJar.exists())
                    optJar.delete()
                JarFile file = new JarFile(jarFile)
                Enumeration enumeration = file.entries()
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                    String entryName = jarEntry.getName()
                    ZipEntry zipEntry = new ZipEntry(entryName)
                    InputStream inputStream = file.getInputStream(jarEntry)
                    jarOutputStream.putNextEntry(zipEntry)
                    if (entryName.equals(InjectInfo.get().injectToClassName + ".class")) {
                        Log.e(NAME, "to class >> " + entryName)
                        ClassReader cr = new ClassReader(inputStream)
                        ClassWriter cw = new ClassWriter(cr, 0)
                        ClassVisitor cv = new InjectClassVisitor(Opcodes.ASM5, cw)
                        cr.accept(cv, ClassReader.EXPAND_FRAMES)
                        def bytes = cw.toByteArray()
                        jarOutputStream.write(bytes)
                    } else {
                        jarOutputStream.write(IOUtils.toByteArray(inputStream))
                    }
                    inputStream.close()
                    jarOutputStream.closeEntry()
                }
                jarOutputStream.close()
                file.close()

                if (jarFile.exists()) {
                    jarFile.delete()
                }
                optJar.renameTo(jarFile)
            } else {
                Log.e(NAME, "Inserting code to class >> " + InjectInfo.get().injectToClass.absolutePath)

                File file = InjectInfo.get().injectToClass
                def optClass = new File(file.getParent(), file.name + ".opt")
                if (optClass.exists())
                    optClass.delete()
                FileInputStream inputStream = new FileInputStream(file)
                FileOutputStream outputStream = new FileOutputStream(optClass)

                ClassReader cr = new ClassReader(inputStream)
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                ClassVisitor cv = new InjectClassVisitor(cw)
                cr.accept(cv, EXPAND_FRAMES)

                byte[] code = cw.toByteArray()
                outputStream.write(code)
                inputStream.close()
                outputStream.close()

                if (file.exists()) {
                    file.delete()
                }
                optClass.renameTo(file)
            }
        }

        cost = (System.currentTimeMillis() - startTime) / 1000
        Log.i(NAME, "========== Transform insert cost $cost secs end ===========")
    }

    /**
     * 扫描jar包
     * @param jarFile
     */
    static void scanJar(File jarFile, File destFile) {
        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            if (isIncludeClass(entryName)) {
//                Log.i(NAME, "--------------------------------------------------------------")
//                Log.e(NAME, "------=== jarFile absolutePath = " + jarFile.absolutePath + " --------- ")
//                Log.e(NAME, "------=== destFile absolutePath = " + destFile.absolutePath + " --------- ")
//                Log.e(NAME, "------=== jarInput.file entryName = " + entryName + " --------- ")
                //todo 3 ...对jar下面的class处理
                InputStream inputStream = file.getInputStream(jarEntry)
                ClassReader cr = new ClassReader(inputStream)
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                ClassVisitor cv = new IClassVisitor(destFile, cw)
                cr.accept(cv, EXPAND_FRAMES)
                inputStream.close()
            }
        }
        file.close()
    }

    /**
     * 扫描文件
     * @param file
     */
    static void scanFile(File file, File dest) {
        def name = file.name
        if (isIncludeClass(name)) {
//            Log.i(NAME, "------=== directoryInputs.file name = " + file.absolutePath + " --------- ")
//            Log.e(NAME, "------=== dest name = " + dest.absolutePath + " --------- ")
            InputStream inputStream = new FileInputStream(file)
            ClassReader cr = new ClassReader(inputStream)
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            ClassVisitor cv = new IClassVisitor(dest, cw)
            cr.accept(cv, EXPAND_FRAMES)
            inputStream.close()
        }
    }

    static boolean excludeJar(String path) {//排除所有google android jar，可自行补充
        if (path.contains("com.android.support")) {
            return true
        } else if (path.contains("/android.arch.")) {
            return true
        } else if (path.contains("/support-")) {
            return true
        } else if (path.contains("/appcompat-")) {
            return true
        } else if (path.contains("/constraint-layout")) {
            return true
        } else if (path.contains("/animated-vector-drawable")) {
            return true
        } else if (path.contains("/android/m2repository")) {
            return true
        } else if (path.contains("com/google")) {
            return true
        } else if (path.contains("android/arch/core")) {
            return true
        } else if (path.contains("javax/annotation")) {
            return true
        } else if (path.contains("com/squareup")) {
            return true
        } else if (path.contains("org/checkerframework")) {
            return true
        }
        return false
    }

    static boolean isIncludeClass(String name) {
        if (name.endsWith(".class")) {
            if (name == null
                    || name.endsWith("R.class")
                    || name.endsWith("BuildConfig.class")
                    || name.contains("android/support/")
                    || name.contains("org/apache/commons")
                    || name.contains("com/google")
                    || name.contains("android/arch")
                    || name.contains("javax/annotation")
                    || name.contains("com/squareup")
                    || name.contains("org/checkerframework")) {
                return false
            }
            return true
        }
        return false
    }


}