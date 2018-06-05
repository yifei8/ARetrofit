#### 最新版本

模块|auto-api|auto-complier|auto-annotation|auto-inject
---|---|---|---|---
最新版本|[![Download](https://api.bintray.com/packages/iyifei/maven/auto-api/images/download.svg)](https://bintray.com/iyifei/maven/auto-api/_latestVersion)|[![Download](https://api.bintray.com/packages/iyifei/maven/auto-complier/images/download.svg)](https://bintray.com/iyifei/maven/auto-complier/_latestVersion)|[![Download](https://api.bintray.com/packages/iyifei/maven/auto-annotation/images/download.svg)](https://bintray.com/iyifei/maven/auto-annotation/_latestVersion)|[![Download](https://api.bintray.com/packages/iyifei/maven/auto-inject/images/download.svg)](https://bintray.com/iyifei/maven/auto-inject/_latestVersion)

> Android组件化超级路由，为简单而生。

### Github 源码: [ARetrofit](https://github.com/yifei8/ARetrofit)

## demo
[demo apk 下载](https://github.com/yifei8/AndroidRetrofit/raw/master/app-release.apk)

## 一 介绍
从命名来看，做Android开发的小伙伴们感觉是不是似曾相识…是的，Retrofit，一款优秀的网络框架，目前正在被大量使用，相信大家对它的用法已经非常熟悉吧。

ARetrofit一款优秀的Android组件化框架（皮一下^_^开心），可以轻松实现跨module通信。这里之所以使用Retrofit作为后缀命名主要是为了尊重retrofit大神的架构思路，其目的降低开发者的学习和使用成本。

如果你正在对项目进行组件化，AndroidRetrofit将是不二选择。

## 二 功能介绍
- 支持跨module通信
- 支持添加多个拦截器，自定义拦截顺序
- 支持依赖注入，可单独作为依赖注入框架使用
- 可单独作为自动注册框架使用
- 支持InstantRun
- 支持MultiDex(Google方案)
- 页面、拦截器、服务等组件均自动注册到框架
- 支持获取Fragment
- 跨进程通信（待完善～）如有IPC业务可参考[ABridge 进程间通信最牛方案](https://www.jianshu.com/p/46134eef5703)

特点：
简单、低侵入(只需要在Activity/Fragment/其他类 声明路由注册)、易上手

## 二 基本用法
### step1: 添加依赖和配置
 ``` gradle
//module gradle file
dependencies {
    ...
    annotationProcessor "com.sjtu.yifei:auto-complier:x.x.x"
    api "com.sjtu.yifei:auto-api:x.x.x"
}

//project gradle file
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        //Gradle 插件实现路由表的自动加载
        classpath "com.sjtu.yifei:auto-inject:x.x.x"
    }
}

// app gradle file
apply plugin: 'com.android.application'
//在plugin:'com.android.application'下添加以下插件，用于自动注入
apply plugin: 'com.sjtu.yifei.autoinject'
```
### step2: 声明路由注解
- Activity
```java
/**
 * this activity in test-module1
 */
@Route(path = "/test-module1/Test1Activity")
public class Test1Activity extends AppCompatActivity {
    ...
}
```
- Fragment
```java
/**
 * this fragment in login-module
 */
@Route(path = "/login-module/TestFragment")
public class TestFragment extends Fragment {
    ...
}
```
### step3: 面向接口编程:AndroidRetrofit将Activity／Fragment 通信转化成接口
```java
/**
 * this interface in router-module
 */
public interface RouteService {
    //Activity 跳转，支持注解传入参数/Flags/requestCode，参数解析遵循android机制
    @Flags(Intent.FLAG_ACTIVITY_NEW_TASK)
    @Go("/test-module1/Test1Activity")
    boolean launchTest1Activity(@Extra("para1") String para1, @Extra("para2") int para2);
    @Go("/test-module1/Test1Activity")
    boolean launchTest1ActivityForResult(@Extra("para1") String para1, @Extra("para2") int para2, @RequestCode int requestCode);

    //Fragment初始化，支持注解传入参数，参数解析遵循android机制
    @Go("/login-module/TestFragment")
    Fragment getTestFragment(@Extra("param1") String para1, @Extra("param2") int[] para2);
}
```
### step4: 初始化SDK
```java
    //在你的application onCreate()方法中
    Routerfit.init(this);
```
### step5: 发起路由操作
```java
private void launchTest1Activity(String para1, int para2) {
    //路由操作
   Routerfit.register(RouteService.class).launchTest1Activity(para1,para2);
}
```
### step6：添加混淆规则(如果使用了Proguard)
```
-keep class * implements com.sjtu.yifei.ioc.**{*;}
-keep class * implements com.sjtu.yifei.annotation.AutoRegisterContract{*;}
```
## 三 高阶用法
- 以登录组件为例
### step1 声明登录服务
```java
/**
 * this interface in router-module
 * 声明登录服务
 */
public interface ILoginProvider {
    String login();
}
```
### 注：#自己声明的服务需要防止混淆#
```
-keep class * implements com.sjtu.yifei.route.ILoginProvider{*;}
```
### step2 实现服务
```java
/**
 * the ILoginProvider in login-module/
 */
@Route(path = "/login-module/ILoginProviderImpl")
public class ILoginProviderImpl implements ILoginProvider {

    private String para1;
    private int para2;

    public ILoginProviderImpl(String para1, int para2) {
        this.para1 = para1;
        this.para2 = para2;
    }

    @Override
    public String login() {
        Routerfit.register(RouteService.class).launchLoginActivity();
        return "ILoginProviderImpl para1:" + para1 + ",para2:" + para2;
    }
}
```
### step3 注册服务接口
```java
public interface RouteService {
    ...
    //通过依赖注入解耦，支持注解传入构造函数参数
    @Go("/login-module/ILoginProviderImpl")
    ILoginProvider getILoginProviderImpl(@Extra("param1") String para1, @Extra("param2") int para2);
}
```
### step4 拦截器功能
```java
//拦截器只需申明注解，不需要额外处理。注：priority 值越大，拦截器优先级越高
@Interceptor(priority = 3)
public class LoginInterceptor implements AInterceptor {

    private static final String TAG = "LoginInterceptor";

    @Override
    public void intercept(Chain chain) {
        Log.e(TAG,"path:" + chain.path());
        //假如 Test2Activity 需要登录
        if ("/login-module/Test2Activity".equalsIgnoreCase(chain.path())) {
            if( /**是否需要登录*/) {//需要登录
                //获取登录服务
                ILoginProvider iProvider = Routerfit.register(RouteService.class).getILoginProviderImpl("provider from login-module", 10001);
                if (iProvider != null) {
                    iProvider.login();
                } else {
                    //传递请求，不执行以下代码则拦截请求
                    chain.proceed();
                }
            }
        } else {
            //传递请求，不执行以下代码则拦截请求
            chain.proceed();
        }
    }
}
```
## 四 沟通交流
- qq群
![qq交流群.jpeg](https://user-gold-cdn.xitu.io/2018/6/5/163cea15e497ee16?w=200&h=274&f=jpeg&s=15655)
- 微信群
![微信群.jpeg](https://user-gold-cdn.xitu.io/2018/6/5/163cea15e51a136a?w=200&h=266&f=jpeg&s=14284)
- Email
yifei8@gmail.com
644912187@qq.com
## 五 欢迎 fork、issues







