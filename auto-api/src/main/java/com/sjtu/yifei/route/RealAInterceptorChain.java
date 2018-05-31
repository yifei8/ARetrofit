package com.sjtu.yifei.route;

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
        // Call the next interceptor in the chain.
        RealAInterceptorChain next = new RealAInterceptorChain(interceptors, index + 1, serviceMethod);
        AInterceptor interceptor = interceptors.get(index);
        interceptor.intercept(next);
        return true;
    }


}
