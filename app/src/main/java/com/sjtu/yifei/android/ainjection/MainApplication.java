package com.sjtu.yifei.android.ainjection;

import android.support.multidex.MultiDexApplication;

import com.sjtu.yifei.launcher.RetrofitConfig;
import com.sjtu.yifei.route.RouteImpl;
import com.sjtu.yifei.route.RouteService;
import com.sjtu.yifei.route.Routerfit;


/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/15
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class MainApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        RouteImpl.getInstance().init(this);
    }
}
