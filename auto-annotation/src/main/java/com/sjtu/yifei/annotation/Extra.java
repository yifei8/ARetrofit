package com.sjtu.yifei.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述：路由参数
 * 创建人：yifei
 * 创建时间：2018/5/10
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Extra {
    String value() default "";
}
