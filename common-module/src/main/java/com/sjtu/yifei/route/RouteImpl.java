package com.sjtu.yifei.route;

import android.content.Context;

import com.sjtu.yifei.launcher.RetrofitConfig;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/16
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class RouteImpl {

    private RouteService routeService;

    private RouteImpl() {
    }

    private static class InstanceHolder {
        private static final RouteImpl instance = new RouteImpl();
    }

    public static RouteImpl getInstance() {
        return InstanceHolder.instance;
    }

    public void init(Context context) {
        RetrofitConfig.init(context);
    }

    public RouteService create() {
        if (routeService == null) {
            synchronized (this) {
                if (routeService == null) {
                    Routerfit routerfit = new Routerfit.Builder().build();
                    routeService = routerfit.create(RouteService.class);
                }
            }
        }
        return routeService;
    }

    public static void launchTest1Activity(String para1, int para2) {
        RouteImpl.getInstance().create().launchTest1Activity(para1, para2);
    }
    public static void launchTest1ActivityForResult(String para1, int para2, int requestCode) {
        RouteImpl.getInstance().create().launchTest1ActivityForResult(para1, para2, requestCode);
    }

    public static void launchTest2Activity(String para1, int para2) {
        RouteImpl.getInstance().create().launchTest2Activity(para1, para2);
    }
}
