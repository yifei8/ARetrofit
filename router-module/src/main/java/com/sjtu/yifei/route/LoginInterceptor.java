package com.sjtu.yifei.route;

import android.util.Log;
import android.widget.Toast;

import com.sjtu.yifei.annotation.Interceptor;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/31
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@Interceptor(priority = 3)
public class LoginInterceptor implements AInterceptor {

    private static final String TAG = "LoginInterceptor";

    @Override
    public void intercept(final Chain chain) {
        Log.e(TAG,"path:" + chain.path());
        //Test2Activity 需要登录
        if ("/login-module/Test2Activity".equalsIgnoreCase(chain.path())) {
            Toast.makeText(ActivityLifecycleMonitor.getTopActivityOrApp(), "Test2Activity 需要登录", Toast.LENGTH_SHORT).show();
            Routerfit.register(RouteService.class).launchLoginActivity(new ActivityCallback() {
                @Override
                public void onActivityResult(int i, Object o) {
                    if (i == Routerfit.RESULT_OK) {
                        Toast.makeText(ActivityLifecycleMonitor.getTopActivityOrApp(), "result:" + i + ",登录成功 data:" + o, Toast.LENGTH_LONG).show();
                        chain.proceed();//登录成功后继续执行
                    } else {
                        Toast.makeText(ActivityLifecycleMonitor.getTopActivityOrApp(), "result:" + i + ", 登录取消", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            chain.proceed();
        }
    }

}
