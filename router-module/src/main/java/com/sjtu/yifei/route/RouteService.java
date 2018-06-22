package com.sjtu.yifei.route;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.sjtu.yifei.annotation.Extra;
import com.sjtu.yifei.annotation.Flags;
import com.sjtu.yifei.annotation.Go;
import com.sjtu.yifei.annotation.RequestCode;
import com.sjtu.yifei.annotation.Uri;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/11
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public interface RouteService {

    @Flags(Intent.FLAG_ACTIVITY_NEW_TASK)
    @Go("/test-module1/Test1Activity")
    boolean launchTest1Activity(@Extra("para1") String para1, @Extra("para2") int para2);

    @Go("/test-module1/Test1Activity")
    boolean launchTest1ActivityForResult(@Extra("para1") String para1, @Extra("para2") int para2, @RequestCode int requestCode);

    @Go("/test-module1/FragmentActivity")
    boolean launchFragmentActivity();

    @Go("/login-module/Test2Activity")
    boolean launchTest2Activity(@Extra("para1") String para1, @Extra("para2") int[] para2);

    @Go("/login-module/LoginActivity")
    boolean launchLoginActivity();

    @Go("/login-module/TestFragment")
    Fragment getTestFragment(@Extra("param1") String para1, @Extra("param2") int[] para2);

    @Go("/login-module/ILoginProviderImpl")
    ILoginProvider getILoginProviderImpl(@Extra("param1") String para1, @Extra("param2") int para2);

    @Go("/kotlin-module/BasicActivity")
    boolean launchBasicActivity(@Extra("param1") String para1, @Extra("param2") int para2);

    @Go("/kotlin-module/KotlinActivity")
    boolean launchKotlinActivity();

    boolean launchSchemeActivity(@Uri String uristring);
}
