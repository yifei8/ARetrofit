package com.sjtu.yifei.asm

import com.sjtu.yifei.utils.InjectInfo
import com.sjtu.yifei.utils.Log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class InjectClassVisitor extends ClassVisitor {

    static final String TAG = "InjectClassVisitor"

    String own
    String method

    InjectClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    InjectClassVisitor(int api, ClassVisitor cv) {
        super(api, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        own = name
        method = InjectInfo.INJECT_TO_CLASS_Method
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        if (name.endsWith(InjectInfo.get().injectToMethodName)) {
            InjectMethodAdapter injectMethodAdapter = new InjectMethodAdapter(methodVisitor)
            return injectMethodAdapter
        } else {
            return methodVisitor
        }
    }

    class InjectMethodAdapter extends MethodVisitor {

        InjectMethodAdapter(MethodVisitor mv) {
            super(Opcodes.ASM5, mv)
        }

        @Override
        void visitInsn(int opcode) {
            Log.e(TAG, "inject to class:")
            Log.e(TAG, own + "{")
            Log.e(TAG, "       public *** " + InjectInfo.get().injectToMethodName + "() {")
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                InjectInfo.get().injectClasses.each { injectClass ->
                    injectClass = injectClass.replace('/', '.')
                    Log.e(TAG, "           " + method + "(\"" + injectClass + "\")")
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                    mv.visitLdcInsn(injectClass)
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, own, method, "(Ljava/lang/String;)V", false)
                }
            }
            Log.e(TAG, "       }")
            Log.e(TAG, "}")
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocal) {
            mv.visitMaxs(maxStack + 4, maxLocal)
        }
    }

}