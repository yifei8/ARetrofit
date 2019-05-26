package com.sjtu.yifei.route;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.sjtu.yifei.exception.RouteNotFoundException;

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
    /** Standard activity result: operation canceled. */
    public static final int RESULT_CANCELED    = 0;
    /** Standard activity result: operation succeeded. */
    public static final int RESULT_OK           = -1;

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

    @SuppressWarnings("unchecked")
    private <T> T create(final Class<T> service) {
        RouterUtil.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }

                ServiceMethod<Object> serviceMethod = (ServiceMethod<Object>) loadServiceMethod(method, args);
                if (!TextUtils.isEmpty(serviceMethod.uristring)) {
                    Call<T> call = (Call<T>) new ActivityCall(serviceMethod);
                    return call.execute();
                }
                if (!isMainThread()) {
                    throw new IllegalStateException("Must be called from main thread");
                }
                try {
                    if (serviceMethod.clazz == null) {
                        throw new RouteNotFoundException("There is no route match the path \"" + serviceMethod.routerPath + "\"");
                    }
                } catch (RouteNotFoundException e) {
                    Toast.makeText(ActivityLifecycleMonitor.getApp(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                if (RouterUtil.isSpecificClass(serviceMethod.clazz, Activity.class)) {
                    Call<T> call = (Call<T>) new ActivityCall(serviceMethod);
                    return call.execute();
                } else if (RouterUtil.isSpecificClass(serviceMethod.clazz, Fragment.class)
                        || RouterUtil.isSpecificClass(serviceMethod.clazz, android.app.Fragment.class)) {
                    Call<T> call = new FragmentCall(serviceMethod);
                    return call.execute();
                } else if (serviceMethod.clazz != null) {
                    Call<T> call = new IProviderCall<>(serviceMethod);
                    return call.execute();
                }

                if (serviceMethod.returnType != null) {
                    if (serviceMethod.returnType == Integer.TYPE) {
                        return -1;
                    } else if (serviceMethod.returnType == Boolean.TYPE) {
                        return false;
                    } else if (serviceMethod.returnType == Long.TYPE) {
                        return 0L;
                    } else if (serviceMethod.returnType == Double.TYPE) {
                        return 0.0d;
                    } else if (serviceMethod.returnType == Float.TYPE) {
                        return 0.0f;
                    } else if (serviceMethod.returnType == Void.TYPE) {
                        return null;
                    } else if (serviceMethod.returnType == Byte.TYPE) {
                        return (byte)0;
                    } else if (serviceMethod.returnType == Short.TYPE) {
                        return (short)0;
                    } else if (serviceMethod.returnType == Character.TYPE) {
                        return null;
                    }
                }
                return null;
            }
        });
    }

    private ServiceMethod<?> loadServiceMethod(Method method, Object[] args) throws ClassNotFoundException {
        ServiceMethod<?> result = null;
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(method, args).build();
                serviceMethodCache.put(method, result);
            } else if (result.args != args) {
                result.updateArgs(args);
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

    public static void setResult(@IntRange(from = -1, to = 0) int result, Object data) {
        if (!isMainThread()) {
            throw new IllegalStateException("Must be called from main thread");
        }
        Activity activity = ActivityLifecycleMonitor.getTopActivity();
        if (activity != null) {
            String key = activity.getClass().getSimpleName();
            Activity hashActivity = ActivityLifecycleMonitor.getSecondLastActivity();
            if (hashActivity != null) {
                String lastActivityHash = String.valueOf(hashActivity.hashCode());
                String lastClassName = hashActivity.getClass().getSimpleName();
                ActivityCallBackManager.getInstance().setResultDelayed(lastActivityHash + lastClassName + key, result, data);
            }
        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

}
