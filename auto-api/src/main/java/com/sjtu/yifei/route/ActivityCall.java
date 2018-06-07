package com.sjtu.yifei.route;

import android.util.Log;

import java.util.List;

/**
 * [description]
 * author: yifei
 * created at 18/5/20 下午8:57
 */

public class ActivityCall implements Call<Boolean> {
    private final ServiceMethod<Object> serviceMethod;

    public ActivityCall(ServiceMethod<Object> serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public Boolean execute() {
        List<AInterceptor> interceptors = RouteRegister.getInstance().getInterceptors();
        interceptors.add(new CallActivityAInterceptor());
        AInterceptor.Chain chain = new RealAInterceptorChain(interceptors, 0, serviceMethod);
        try {
            return chain.proceed();
        } catch (Exception e) {
            Log.e("auto-api", e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

}
