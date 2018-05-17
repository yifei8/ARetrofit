package com.sjtu.yifei.route;

import com.sjtu.yifei.annotation.Extra;
import com.sjtu.yifei.annotation.Go;
import com.sjtu.yifei.annotation.RequestCode;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/11
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public interface RouteService {

    @Go("/test-module1/Test1Activity")
    String launchTest1Activity(@Extra("para1") String para1, @Extra("para2") int para2);

    @Go("/test-module1/Test1Activity")
    String launchTest1ActivityForResult(@Extra("para1") String para1, @Extra("para2") int para2, @RequestCode int requestCode);

    @Go("/test-module2/Test2Activity")
    String launchTest2Activity(@Extra("para1") String para1, @Extra("para2") int para2);
}
