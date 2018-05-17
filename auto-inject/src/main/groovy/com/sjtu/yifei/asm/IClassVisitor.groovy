package com.sjtu.yifei.asm

import com.sjtu.yifei.utils.InjectInfo
import com.sjtu.yifei.utils.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * 定义在读取Class字节码时会触发的事件，如类头解析完成、注解解析、字段解析、方法解析等。
 */
class IClassVisitor extends ClassVisitor {

    static final String TAG = "IClassVisitor"

    File injectToClass
    String injectToClassName
    String injectClassName

    IClassVisitor(File injectToClass, ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
        this.injectToClass = injectToClass
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        Log.i(TAG, "------------ visit name " + name + ", signature " + signature + ", superName " + superName + ", interfaces " + interfaces.toString() + " ------------")
        super.visit(version, access, name, signature, superName, interfaces)
        injectClassName = name
        for (String i: interfaces) {
            if (InjectInfo.INJECT_TO_CLASS.equals(i)) {
                injectToClassName = name
            }
        }
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//        Log.i(TAG, "------------ visitAnnotation desc " + desc + ", InjectInfo.INJECT_CLASS " + InjectInfo.INJECT_CLASS + " ------------")
        if (InjectInfo.INJECT_CLASS.equals(desc)) {
            if (!InjectInfo.get().injectClasses.contains(injectClassName)) {
                InjectInfo.get().injectClasses.add(injectClassName)
            }
        }
        return super.visitAnnotation(desc, visible)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        Log.i(TAG, "------------ visitMethod name " + name + ", signature " + signature + ", desc " + desc + ", exceptions " + exceptions.toString() + " ------------")
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        IMethodVisitor iMethodVisitor = new IMethodVisitor(injectToClass, injectToClassName, name, methodVisitor)
        return iMethodVisitor
    }

    @Override
    void visitEnd() {
//        Log.i(TAG, "------------ visitEnd ------------")
        super.visitEnd()
    }
}