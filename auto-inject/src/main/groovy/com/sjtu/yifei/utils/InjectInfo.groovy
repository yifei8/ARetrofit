package com.sjtu.yifei.utils

import com.sjtu.yifei.annotation.IMethod
import com.sjtu.yifei.annotation.Inject
import org.objectweb.asm.Type

class InjectInfo {
    static final String INJECT_METHOD = Type.getDescriptor(IMethod.class)

    static final String INJECT_CLASS = Type.getDescriptor(Inject.class)

    static final String INJECT_TO_CLASS = "com/sjtu/yifei/annotation/AutoRegisterContract"
    static final String INJECT_TO_CLASS_Method = "register"

    List<String> injectClasses = new ArrayList<>()

    File injectToClass //最终要注入代码的class

    String injectToClassName  //被注入class name

    String injectToMethodName  //被注入class method name

    static final InjectInfo info = new InjectInfo()
    static InjectInfo get() {
        return info
    }
}