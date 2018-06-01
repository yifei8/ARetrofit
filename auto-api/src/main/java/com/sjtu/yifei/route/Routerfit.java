package com.sjtu.yifei.route;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.sjtu.yifei.util.ActivityLifecycleMonitor;

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

    public static void init(@NonNull Application application) {
        ActivityLifecycleMonitor.init(application);
    }

    private static class InstanceHolder {
        private static final Routerfit instance = new Routerfit.Builder().build();
    }

    private static Routerfit getInstance() {
        return InstanceHolder.instance;
    }

    private Routerfit() {
        RouteRegister.getInstance().init();
    }

    public static <T> T register(Class<T> service) {
        Routerfit routerfit = getInstance();
        return routerfit.create(service);
    }

    private  <T> T create(final Class<T> service) {
        RouterUtil.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                ServiceMethod<Object> serviceMethod = (ServiceMethod<Object>) loadServiceMethod(method, args);
                if (RouterUtil.isSpecificClass(serviceMethod.clazz, Activity.class)) {
                    Call<T> call = (Call<T>) new ActivityCall(serviceMethod);
                    return call.execute();
                } else if (RouterUtil.isSpecificClass(serviceMethod.clazz, Fragment.class)
                        || RouterUtil.isSpecificClass(serviceMethod.clazz, android.app.Fragment.class)) {
                    Call<T> call = new FragmentCall(serviceMethod);
                    return call.execute();
                } else {
                    Call<T> call = new IProviderCall<>(serviceMethod);
                    return call.execute();
                }
            }
        });
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

    private static final class Builder {

        private Builder() {
        }

        private Routerfit build() {
            return new Routerfit();
        }
    }

}
