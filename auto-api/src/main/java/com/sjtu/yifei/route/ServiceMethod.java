package com.sjtu.yifei.route;

import android.text.TextUtils;

import com.sjtu.yifei.annotation.Extra;
import com.sjtu.yifei.annotation.Flags;
import com.sjtu.yifei.annotation.Go;
import com.sjtu.yifei.annotation.RequestCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午7:41
 */

final class ServiceMethod<T> {

    final Class clazz;
    final Map<String, Object> params;
    final int flag;
    final int requestCode;
    final Type returnType;
    final String routerPath;

    ServiceMethod(Builder<T> builder) {
        this.clazz = builder.clazz;
        this.params = builder.params;
        this.requestCode = builder.requestCode;
        this.flag = builder.flag;
        this.returnType = builder.returnType;
        this.routerPath = builder.routerPath;
    }

    static final class Builder<T> {
        Method method;
        Object[] args;
        int flag;
        Type returnType;
        String routerPath;

        Class clazz;
        Map<String, Object> params;
        int requestCode;

        Builder(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        ServiceMethod build() throws ClassNotFoundException {
            returnType = method.getGenericReturnType();

            Go routPath = method.getAnnotation(Go.class);
            if (routPath != null) {
                routerPath = routPath.value();
            }
            if (!TextUtils.isEmpty(routerPath)) {
                clazz = RouteRegister.getInstance().getRouteMap().get(routerPath);
            }

            Flags flagInt = method.getAnnotation(Flags.class);
            if (flagInt != null) {
                flag = flagInt.value();
            }

            params = new HashMap<>();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                if (annotations != null) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Extra) {
                            String key = ((Extra) annotation).value();
                            Object value = args[i];
                            params.put(key, value);
                        } else if (annotation instanceof RequestCode) {
                            requestCode = (int) args[i];
                        }
                    }
                }
            }

            return new ServiceMethod<T>(this);
        }
    }

}
