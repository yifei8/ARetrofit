package com.sjtu.yifei.exception;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/6/7
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String reason) {
        super(reason);
    }
}
