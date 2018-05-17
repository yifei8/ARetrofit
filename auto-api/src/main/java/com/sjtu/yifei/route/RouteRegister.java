package com.sjtu.yifei.route;

import android.text.TextUtils;
import android.util.Log;

import com.sjtu.yifei.annotation.AutoRegisterContract;
import com.sjtu.yifei.annotation.IMethod;
import com.sjtu.yifei.ioc.RouteInject;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/11
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class RouteRegister implements AutoRegisterContract {

    private static final String TAG = "RouteRegister";

    private Map<String, String> routeMap;

    private RouteRegister() {
        routeMap = new HashMap<>();
    }

    private static class InstanceHolder {
        private static final RouteRegister instance = new RouteRegister();
    }

    public static RouteRegister getInstance() {
        return InstanceHolder.instance;
    }

    protected Map<String, String> getRouteMap() {
        return routeMap;
    }

    @IMethod
    public void init() {

    }

    @Override
    public void register(String className) {
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.getConstructor().newInstance();
                if (obj instanceof RouteInject) {
                    Map<String, String> map = ((RouteInject) obj).getRouteMap();
                    if (map != null) {
                        routeMap.putAll(map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
