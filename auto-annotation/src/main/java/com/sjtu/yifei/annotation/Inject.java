package com.sjtu.yifei.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述：用于标记需要被注入类，最近都将插入到标记了#com.sjtu.yifei.annotation.IMethod的方法中
 * 创建人：yifei
 * 创建时间：2018/4/24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Inject {
    String value() default "";
}
