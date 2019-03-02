package com.sjtu.yifei.route;

import android.text.TextUtils;

import com.sjtu.yifei.annotation.Extra;
import com.sjtu.yifei.annotation.Flags;
import com.sjtu.yifei.annotation.Go;
import com.sjtu.yifei.annotation.RequestCode;
import com.sjtu.yifei.annotation.Uri;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午7:41
 */

final class ServiceMethod<T> {

    final Class clazz;
    final Object[] args;
    final Map<String, Object> params;
    final int flag;
    final Type returnType;
    final String routerPath;
    int requestCode;
    String uristring;
    ActivityCallback callback;

    final Map<String, Integer> paramIndexes;
    private int callbackIndex;
    private int uristringIndex;
    private int requestCodeIndex;

    private ServiceMethod(Builder<T> builder) {
        this.clazz = builder.clazz;
        this.args = builder.args;
        this.params = builder.params;
        this.requestCode = builder.requestCode;
        this.flag = builder.flag;
        this.returnType = builder.returnType;
        this.routerPath = builder.routerPath;
        this.uristring = builder.uristring;
        this.callback = builder.callback;
        this.paramIndexes = builder.paramIndexes;
        this.callbackIndex = builder.callbackIndex;
        this.uristringIndex = builder.uristringIndex;
        this.requestCodeIndex = builder.requestCodeIndex;
    }

    void updateArgs(Object[] newArgs) {
        if (newArgs == null || newArgs.length == 0) {
            return;
        }
        if (paramIndexes != null && paramIndexes.size() > 0) {
            for (Map.Entry<String, Integer> entry : paramIndexes.entrySet()) {
                String key = entry.getKey();
                int index = paramIndexes.get(key);
                if (isLegalIndex(index, newArgs)) {
                    Object value = newArgs[index];
                    params.put(key, value);
                }
            }
        }
        if (isLegalIndex(callbackIndex, newArgs)) {
            Object value = newArgs[callbackIndex];
            if (value instanceof ActivityCallback) {
                callback = (ActivityCallback) value;
            }
        }
        if (isLegalIndex(requestCodeIndex, newArgs)) {
            requestCode = (int) newArgs[requestCodeIndex];
        }
        if (isLegalIndex(uristringIndex, newArgs)) {
            uristring = (String) newArgs[uristringIndex];
        }

    }

    private boolean isLegalIndex(int index, Object[] newArgs) {
        return index >= 0 && index < newArgs.length;
    }

    static final class Builder<T> {
        Method method;
        Object[] args;
        int flag;
        Type returnType;
        String routerPath;
        String uristring;

        Class clazz;
        Map<String, Object> params;
        int requestCode;
        ActivityCallback callback;

        Map<String, Integer> paramIndexes;
        int callbackIndex = -1;
        int uristringIndex = -1;
        int requestCodeIndex = -1;

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

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            if (parameterAnnotations != null) {
                params = new HashMap<>();
                paramIndexes = new HashMap<>();
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    Annotation[] annotations = parameterAnnotations[i];
                    if (annotations != null) {
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof Extra) {
                                String key = ((Extra) annotation).value();
                                Object value = args[i];
                                if (value instanceof ActivityCallback) {
                                    callback = (ActivityCallback) value;
                                    callbackIndex = i;
                                } else {
                                    params.put(key, value);
                                    paramIndexes.put(key, i);
                                }
                            } else if (annotation instanceof RequestCode) {
                                requestCode = (int) args[i];
                                requestCodeIndex = i;
                            } else if (annotation instanceof Uri) {
                                uristring = (String) args[i];
                                uristringIndex = i;
                            }
                        }
                    }
                }
            }
            return new ServiceMethod<T>(this);
        }
    }

}
