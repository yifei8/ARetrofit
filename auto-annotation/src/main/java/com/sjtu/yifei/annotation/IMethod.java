package com.sjtu.yifei.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述：用于标记注册代码将插入到此方法中
 * 创建人：yifei
 * 创建时间：2018/4/24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface IMethod {
    String value() default "";
}
