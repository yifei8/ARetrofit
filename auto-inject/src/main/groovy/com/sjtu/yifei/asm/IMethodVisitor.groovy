package com.sjtu.yifei.asm

import com.sjtu.yifei.utils.InjectInfo
import com.sjtu.yifei.utils.Log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IMethodVisitor extends MethodVisitor {

    static final String TAG = "IMethodVisitor"

    File injectToClass
    String injectToClassName
    String methodName

    IMethodVisitor(File file, String injectToClassName, String method, MethodVisitor mv) {
        super(Opcodes.ASM5, mv)
        this.injectToClass = file
        this.injectToClassName = injectToClassName
        this.methodName = method
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (InjectInfo.INJECT_METHOD.equals(desc)) {
            if (injectToClassName != null) {
                InjectInfo.get().injectToClass = injectToClass
//                Log.e(TAG, "------------ injectToClass.absolutePath:" + InjectInfo.get().injectToClass.absolutePath +" ------------")
//                Log.e(TAG, "------------ injectToClassName:" + injectToClassName +" ------------")
//                Log.e(TAG, "------------ methodName:" + methodName +" ------------")
                InjectInfo.get().injectToClassName = injectToClassName
                InjectInfo.get().injectToMethodName = methodName
            } else {
                Log.e(TAG, "the method class must implement com.sjtu.yifei.annotation.AutoRegisterContract if the method annotations include @IMethod")
            }
        }
        return super.visitAnnotation(desc, visible)
    }

}