package com.sjtu.yifei.route;

import android.text.TextUtils;

import com.sjtu.yifei.annotation.Extra;
import com.sjtu.yifei.annotation.Go;
import com.sjtu.yifei.annotation.RequestCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * [description]
 * author: yifei
 * created at 18/5/11 下午10:27
 */

public class Routerfit {
    private static final String TAG = "Routerfit";

    private Routerfit() {
        RouteRegister.getInstance().init();
    }

    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                Go routPath = method.getAnnotation(Go.class);
                String path = null;
                if (routPath != null) {
                    path = routPath.value();
                }
                Map<String, Object> params = new HashMap<>();
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
                                //todo 这里可能会抛出异常，当args[i] 不是int 的时候，需要处理
                            }
                        }
                    }
                }
                String className = RouteRegister.getInstance().getRouteMap().get(path);
                if (!TextUtils.isEmpty(className)) {
                    Class<?> actClazz = Class.forName(className);
                    ActivityCall activityCall = new ActivityCall.Builder()
                            .params(params)
                            .activityClass(actClazz)
                            .requestCode(requestCode)
                            .build();
                    activityCall.launch();
                }

                String result = "go route (path:" + path + "-----> className:" + className + "), request params:" + params.toString();
                return result;
            }
        });
    }

    public static final class Builder {
        public Builder() {
        }

        public Routerfit build() {
            return new Routerfit();
        }
    }

}
