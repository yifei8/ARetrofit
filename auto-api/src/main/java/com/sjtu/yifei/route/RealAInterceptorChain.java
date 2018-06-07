package com.sjtu.yifei.route;

import android.util.Log;

import java.util.List;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/31
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public final class RealAInterceptorChain implements AInterceptor.Chain  {

    private List<AInterceptor> interceptors;
    private ServiceMethod serviceMethod;
    private final int index;
    private boolean result;

    RealAInterceptorChain(List<AInterceptor> interceptors, int index, ServiceMethod serviceMethod) {
        this.interceptors = interceptors;
        this.serviceMethod = serviceMethod;
        this.index = index;
    }

    public String path() {
        return serviceMethod.routerPath;
    }

    @Override
    public ServiceMethod serviceMethod() {
        return serviceMethod;
    }

    @Override
    public boolean proceed() {
        if (index >= interceptors.size()) throw new AssertionError();
        try {
            RealAInterceptorChain next = new RealAInterceptorChain(interceptors, index + 1, serviceMethod);
            AInterceptor interceptor = interceptors.get(index);
            interceptor.intercept(next);
            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e("auto-api", e.getLocalizedMessage());
            return false;
        } catch (Exception e) {
            Log.e("auto-api", e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void proceedResult(boolean result) {
        this.result = result;
    }


}
