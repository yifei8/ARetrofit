package com.sjtu.yifei.route;

/**
 * 类描述：Activity 跳转拦截器
 * 创建人：yifei
 * 创建时间：2018/5/31
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public interface AInterceptor {

    void intercept(Chain chain) throws Exception;

    interface Chain {

        String path();

        ServiceMethod serviceMethod();

        boolean proceed();

        void proceedResult(boolean result);
    }
}
