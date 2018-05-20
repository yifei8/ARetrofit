package com.sjtu.yifei.route;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.sjtu.yifei.util.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [description]
 * author: yifei
 * created at 18/5/11 下午10:27
 */

public final class Routerfit {
    private static final String TAG = "Routerfit";

    private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();

    private Routerfit() {
        RouteRegister.getInstance().init();
    }

    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                ServiceMethod<Object> serviceMethod = (ServiceMethod<Object>) loadServiceMethod(method, args);
                if (isSpecificClass(serviceMethod.clazz, Activity.class)) {
                    Call<T> call = new ActivityCall<>(serviceMethod);
                    return call.execute();
                } else if (isSpecificClass(serviceMethod.clazz, Fragment.class)
                        || isSpecificClass(serviceMethod.clazz, android.app.Fragment.class)) {
                    Call<T> call = new FragmentCall<>(serviceMethod);
                    return call.execute();
                }
                return false;
            }
        });
    }

    private boolean isSpecificClass(Class clazz, Class youSpecific) {
        while (clazz != Object.class) {
            if (clazz == youSpecific) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    private ServiceMethod<?> loadServiceMethod(Method method, Object[] args) throws ClassNotFoundException {
        ServiceMethod<?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(method, args).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public static final class Builder {

        public Builder() {
        }

        public Routerfit build() {
            return new Routerfit();
        }
    }

}
