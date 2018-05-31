package com.sjtu.yifei.route;

import android.util.Log;

import com.sjtu.yifei.annotation.Interceptor;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/31
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@Interceptor(priority = 5)
public class LoginInterceptor2 implements AInterceptor {

    private static final String TAG = "LoginInterceptor";

    @Override
    public void intercept(Chain chain) {
        Log.e(TAG,"path2:" + chain.path());
//        chain.proceed();
    }
}
