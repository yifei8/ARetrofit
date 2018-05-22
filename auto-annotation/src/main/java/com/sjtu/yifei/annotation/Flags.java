package com.sjtu.yifei.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/22
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Flags {
    int value() default -1;
}
