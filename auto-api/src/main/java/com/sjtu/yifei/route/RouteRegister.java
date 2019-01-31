package com.sjtu.yifei.route;

import android.text.TextUtils;
import android.util.Log;

import com.sjtu.yifei.annotation.IMethod;
import com.sjtu.yifei.annotation.InjectContract;
import com.sjtu.yifei.ioc.AInterceptorInject;
import com.sjtu.yifei.ioc.RouteInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/11
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public final class RouteRegister implements InjectContract {

    private static final String TAG = "RouteRegister";

    private Map<String, Class<?>> routeMap;
    private List<AInterceptor> interceptors = new ArrayList<>();

    private RouteRegister() {
        routeMap = new HashMap<>();
    }

    private static class InstanceHolder {
        private static final RouteRegister instance = new RouteRegister();
    }

    static RouteRegister getInstance() {
        return InstanceHolder.instance;
    }

    Map<String, Class<?>> getRouteMap() {
        return routeMap;
    }

    List<AInterceptor> getInterceptors() {
        return interceptors;
    }

    @IMethod
    public void init() {

    }

    @Override
    public void injectClass(String className) {
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.getConstructor().newInstance();
                if (obj instanceof RouteInject) {
                    Map<String, Class<?>> map = ((RouteInject) obj).getRouteMap();
                    if (map != null) {
                        routeMap.putAll(map);
                    }
                } else if (obj instanceof AInterceptorInject) {
                    Map<Integer, Class<?>> integerClassMap = ((AInterceptorInject) obj).getAInterceptors();
                    if (integerClassMap != null && integerClassMap.size() > 0) {
                        List<Map.Entry<Integer, Class<?>>> infoIds = new ArrayList<>(integerClassMap.entrySet());
                        // 对HashMap中的key 进行排序
                        Collections.sort(infoIds, new Comparator<Map.Entry<Integer, Class<?>>>() {
                            public int compare(Map.Entry<Integer, Class<?>> o1,
                                               Map.Entry<Integer, Class<?>> o2) {
                                return o2.getKey() - o1.getKey();
                            }
                        });

                        for (Map.Entry<Integer, Class<?>> entry : infoIds) {
                            Class<?> aInterceptorClassName = entry.getValue();
                            try {
                                Class<? extends AInterceptor> interceptorClass = (Class<? extends AInterceptor>) aInterceptorClassName;
                                AInterceptor iInterceptor = interceptorClass.getConstructor().newInstance();
                                interceptors.add(iInterceptor);
                            } catch (Exception ex) {
                                Log.e("auto-api", "" + ex.getMessage());
                                throw new RuntimeException(TAG + "ARouter init interceptor error! name = [" + aInterceptorClassName.getSimpleName() + "], reason = [" + ex.getMessage() + "]");
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
