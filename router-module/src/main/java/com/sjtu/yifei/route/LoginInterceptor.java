package com.sjtu.yifei.route;

import android.util.Log;
import android.widget.Toast;

import com.sjtu.yifei.annotation.Interceptor;
import com.sjtu.yifei.util.Utils;

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
    public void intercept(Chain chain) {
        Log.e(TAG,"path:" + chain.path());
        //Test2Activity 需要登录
        if ("/login-module/Test2Activity".equalsIgnoreCase(chain.path())) {
            Toast.makeText(Utils.getTopActivityOrApp(), "Test2Activity 需要登录", Toast.LENGTH_SHORT).show();
            ILoginProvider iProvider = RouteImpl.getILoginProviderImpl("provider from login-module", 10001);
            if (iProvider != null) {
                iProvider.login();
            }
        } else {
            chain.proceed();
        }
    }
}
