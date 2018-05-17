package com.sjtu.yifei.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述：路由
 * 创建人：yifei
 * 创建时间：2018/5/10
 * 修改人：
 * 修改时间：
 * 修改备注：
 * <p>
 * java中元注解有四个： @Retention @Target @Document @Inherited；
 * <p>
 * @Retention: 注解的保留位置
 *      RetentionPolicy.SOURCE   //注解仅存在于源码中，在class字节码文件中不包含
 *      RetentionPolicy.CLASS    // 默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得，
 *      RetentionPolicy.RUNTIME  // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
 *
 * @Target: 注解的作用目标
 *      ElementType.TYPE              //接口、类、枚举、注解
 *      ElementType.FIELD             //字段、枚举的常量
 *      ElementType.METHOD            //方法
 *      ElementType.PARAMETER         //方法参数
 *      ElementType.CONSTRUCTOR       //构造函数
 *      ElementType.LOCAL_VARIABLE    //局部变量
 *      ElementType.ANNOTATION_ROUTE   //注解
 *      ElementType.PACKAGE           //包
 *
 * @Document: 说明该注解将被包含在javadoc中
 *
 * @Inherited: 说明子类可以继承父类中的该注解
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Route {
    /**
     * Path of route
     */
    String path();
}
