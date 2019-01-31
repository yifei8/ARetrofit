package com.sjtu.yifei.annotation;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/4/27
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public interface InjectContract {

    /**
     *
     * "@Inject" 注解标示的class 最终都会注入到该"@IMethod"注解标示过的方法中
     *  注："@IMethod"注解标示过的方法将由编译器自动注入实现代码，注入最终的代码如下如：
     *
     * @IMethod
     * public void iMethodName() {
     *       injectClass("injectClassName1")
     *       injectClass("injectClassName2")
     *       injectClass("injectClassName3")
     *       injectClass("injectClassName4")
     * }
     *
     * 用户可以在该方法中通过反射完成自己的业务需求
     * @param className class name
     */
    void injectClass(String className);
}
