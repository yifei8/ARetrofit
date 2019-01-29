# ARetrofit

> 开源ARetrofit大约半年左右的时间，没有任何推广和介绍，今天一看也有160+的stars了，这里非常感谢大家的支持。趁着年关降至，也想写一点东西来介绍一下这个框架。无论是ARetrofit的用户也好，还是对源码感兴趣的同学也好，希望能从这篇文章中有所收获。

# 简介
ARetrofit是一款针对Android组件之间通信的框架，实现组件之间解耦的同时还可以通信。

# 组件化
Android组件化已经不是一个新鲜的概念了，出来了已经有很长一段时间了，大家可以自行Google，可以看到一堆相关的文章。

简单的来说，所谓的组件就是Android Studio中的Module，每一个Module都遵循高内聚的原则，通过ARetrofit来实现无耦合的代码结构，如下图：

![Figure 1](https://github.com/yifei8/ARetrofit/blob/dev/wiki_res/%E7%BB%84%E4%BB%B6%E5%8C%96%E7%BB%93%E6%9E%84.png?raw=true)

每一个Module可单独作为一个project运行，而打包到整体时Module之间的通信通过ARetrofit完成。

# ARetrofit原理
讲原理之前，我想先说说为什么要ARetrofit。开发ARetrofit这个项目的思路来源其实是Retrofit，Retrofit是Square公司开发的一款针对Android网络请求的框架，这里不对Retrofit展开来讲。主要是Retrofit框架使用非常多的设计模式，可以说Retrofit这个开源项目将Java的设计模式运用到了极致，当然最终提供的API也是非常简洁的。如此简洁的API，使得我们APP中的网络模块实现变得非常轻松，并且维护起来也很舒服。因此我觉得有必要对Android组件之间对通信也变得轻松，使用者可以通过简洁的API就可以实现通信，更重要的是维护起来也非常的舒服。

ARetrofit基本原理可以简化为下图所示：
![Figure 2. 基本原理](https://github.com/yifei8/ARetrofit/blob/dev/wiki_res/%E5%9F%BA%E6%9C%AC%E5%8E%9F%E7%90%86%E5%9B%BE.png?raw=true)

1. 通过注解声明需要通信的Activity／Fragment或者Class
2. 每一个module通过annotationProcessor (APT作者已经不维护啦)在编译时生成待注入的RouteInject的实现类和AInterceptorInject的实现类。
这一步在执行app[build]时会输出日志，可以直观的看到，如下图所示：
```
注: AInjecton::Compiler >>> Apt interceptor Processor start... <<<
注: AInjecton::Compiler enclosindClass = null
注: AInjecton::Compiler value = 3
注: AInjecton::Compiler auto generate class = com$$sjtu$$yifei$$eCGVmTMvXG$$AInterceptorInject
注: AInjecton::Compiler add path= 3 and class= LoginInterceptor
....
注: AInjecton::Compiler >>> Apt route Processor start... <<<
注: AInjecton::Compiler enclosindClass = null
注: AInjecton::Compiler value = /login-module/ILoginProviderImpl
注: AInjecton::Compiler enclosindClass = null
注: AInjecton::Compiler value = /login-module/LoginActivity
注: AInjecton::Compiler enclosindClass = null
注: AInjecton::Compiler value = /login-module/Test2Activity
注: AInjecton::Compiler enclosindClass = null
注: AInjecton::Compiler value = /login-module/TestFragment
注: AInjecton::Compiler auto generate class = com$$sjtu$$yifei$$VWpdxWEuUx$$RouteInject
注: AInjecton::Compiler add path= /login-module/TestFragment and class= null
注: AInjecton::Compiler add path= /login-module/LoginActivity and class= null
注: AInjecton::Compiler add path= /login-module/Test2Activity and class= null
注: AInjecton::Compiler add path= /login-module/ILoginProviderImpl and class= null
注: AInjecton::Compiler >>> Apt route Processor succeed <<<
```
3. 将编译时生成的类注入到RouterRegister中，这个类主要用于维护路由表和拦截器，对应的[build]日志如下：
```
TransformPluginLaunch >>> ========== Transform scan start ===========
TransformPluginLaunch >>> ========== Transform scan end cost 0.238 secs and start inserting ===========
TransformPluginLaunch >>> Inserting code to jar >> /Users/yifei/as_workspace/ARetrofit/app/build/intermediates/transforms/TransformPluginLaunch/release/8.jar
TransformPluginLaunch >>> to class >> com/sjtu/yifei/route/RouteRegister.class
InjectClassVisitor >>> inject to class:
InjectClassVisitor >>> com/sjtu/yifei/route/RouteRegister{
InjectClassVisitor >>>        public *** init() {
InjectClassVisitor >>>            register("com.sjtu.yifei.FBQWNfbTpY.com$$sjtu$$yifei$$FBQWNfbTpY$$RouteInject")
InjectClassVisitor >>>            register("com.sjtu.yifei.klBxerzbYV.com$$sjtu$$yifei$$klBxerzbYV$$RouteInject")
InjectClassVisitor >>>            register("com.sjtu.yifei.JmhcMMUhkR.com$$sjtu$$yifei$$JmhcMMUhkR$$RouteInject")
InjectClassVisitor >>>            register("com.sjtu.yifei.fpyxYyTCRm.com$$sjtu$$yifei$$fpyxYyTCRm$$AInterceptorInject")
InjectClassVisitor >>>        }
InjectClassVisitor >>> }
TransformPluginLaunch >>> ========== Transform insert cost 0.017 secs end ===========
```
4. Routerfit.register(Class<T> service) 这一步主要是通过动态代理模式实现接口中声明的服务。

> 前面讲的是整体的框架设计思想，便于读者从全局的觉得来理解ARetrofit的框架的架构。接下来，将待大家个个击破上面提到的annotationProcessor、 transform、动态代理、拦截器功能的实现等细节。

# 那么annotationProcessor是什么

