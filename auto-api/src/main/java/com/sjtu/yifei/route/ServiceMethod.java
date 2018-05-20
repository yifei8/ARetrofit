package com.sjtu.yifei.route;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.sjtu.yifei.annotation.Extra;
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
    final int requestCode;

    ServiceMethod(Builder<T> builder) {
        this.clazz = builder.clazz;
        this.params = builder.params;
        this.requestCode = builder.requestCode;
    }

    static final class Builder<T> {
        Method method;
        Object[] args;

        Class clazz;
        Map<String, Object> params;
        int requestCode;

        Builder(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder aClass(Class aClass) {
            this.clazz = aClass;
            return this;
        }

        public Builder requestCode(int aClass) {
            this.requestCode = requestCode;
            return this;
        }

        public ServiceMethod build() throws ClassNotFoundException {
            Type returnType = method.getGenericReturnType();

            Go routPath = method.getAnnotation(Go.class);
            String path = null;
            if (routPath != null) {
                path = routPath.value();
            }
            String className = RouteRegister.getInstance().getRouteMap().get(path);
            if (!TextUtils.isEmpty(className)) {
                clazz = Class.forName(className);
            }

            params = new HashMap<>();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int requestCode = -1;
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
