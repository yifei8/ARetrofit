# ARetrofit

> 开源ARetrofit大约半年左右的时间，没有任何推广和介绍，今天一看也有160+的stars了，这里非常感谢大家的支持。趁着年关降至，也想写一点东西来介绍一下这个框架。无论是ARetrofit的用户也好，还是对源码感兴趣的同学也好，希望能从这篇文章中有所收获。

# 简介
ARetrofit是一款针对Android组件之间通信的框架，实现组件之间解耦的同时还可以通信。

# [源码链接](https://github.com/yifei8/ARetrofit)
欢迎star、issues、fork

# 组件化
Android组件化已经不是一个新鲜的概念了，出来了已经有很长一段时间了，大家可以自行Google，可以看到一堆相关的文章。

简单的来说，所谓的组件就是Android Studio中的Module，每一个Module都遵循高内聚的原则，通过ARetrofit来实现无耦合的代码结构，如下图：

![Figure 1](https://github.com/yifei8/ARetrofit/blob/dev/wiki_res/%E7%BB%84%E4%BB%B6%E5%8C%96%E7%BB%93%E6%9E%84.png?raw=true)

每一个Module可单独作为一个project运行，而打包到整体时Module之间的通信通过ARetrofit完成。

# ARetrofit原理
讲原理之前，我想先说说为什么要ARetrofit。开发ARetrofit这个项目的思路来源其实是Retrofit，Retrofit是Square公司开发的一款针对Android网络请求的框架，这里不对Retrofit展开来讲。主要是Retrofit框架使用非常多的设计模式，可以说Retrofit这个开源项目将Java的设计模式运用到了极致，当然最终提供的API也是非常简洁的。如此简洁的API，使得我们APP中的网络模块实现变得非常轻松，并且维护起来也很舒服。因此我觉得有必要将Android组件之间的通信也变得轻松，使用者可以优雅的通过简洁的API就可以实现通信，更重要的是维护起来也非常的舒服。

ARetrofit基本原理可以简化为下图所示：
![Figure 2. 基本原理](https://github.com/yifei8/ARetrofit/blob/dev/wiki_res/%E5%9F%BA%E6%9C%AC%E5%8E%9F%E7%90%86%E5%9B%BE.png?raw=true)

1. 通过注解声明需要通信的Activity／Fragment或者Class
2. 每一个module通过annotationProcessor在编译时生成待注入的RouteInject的实现类和AInterceptorInject的实现类。
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

> 前面讲的是整体的框架设计思想，便于读者从全局的觉得来理解ARetrofit的框架的架构。接下来，将待大家个个击破上面提到的annotationProcessor、 transform在项目中如何使用，以及动态代理、拦截器功能的实现等细节。

## 一、annotationProcessor生成代码
annotationProcessor(注解处理器)是javac内置的一个用于编译时扫描和处理注解(Annotation)的工具。简单的说，在源代码编译阶段，通过注解处理器，我们可以获取源文件内注解(Annotation)相关内容。Android Gradle 2.2 及以上版本提供annotationProcessor的插件。
在ARetrofit中annotationProcessor对应的module是auto-complier，在使用annotationProcessor之前首先需要声明好注解。关于注解不太了解或者遗忘的同学可直接参考我之前写的[Java注解](https://www.jianshu.com/p/ef1146a771b5)这篇文章，本项目中声明的注解在auto-annotation这个module中，主要有：
- @Extra 路由参数
- @Flags intent flags
- @Go 路由路径key
- @Interceptor 声明自定义拦截器
- @RequestCode 路由参数
- @Route路由
- @Uri
- @IMethod 用于标记注册代码将插入到此方法中(transform中使用)
- @Inject 用于标记需要被注入类，最近都将插入到标记了#com.sjtu.yifei.annotation.IMethod的方法中(transform中使用)

创建自定义的注解处理器，具体使用方法可参考[利用注解动态生成代码](https://blog.csdn.net/Gaugamela/article/details/79694302)，本项目中的注解处理器如下所示：
```
//这是用来注册注解处理器要处理的源代码版本。
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//这个注解用来注册注解处理器要处理的注解类型。有效值为完全限定名（就是带所在包名和路径的类全名
@SupportedAnnotationTypes({ANNOTATION_ROUTE, ANNOTATION_GO})
//来注解这个处理器，可以自动生成配置信息
@AutoService(Processor.class)
public class IProcessor extends AbstractProcessor {


}
```
生成代码的关键部分在`GenerateAInterceptorInjectImpl` 和 `GenerateRouteInjectImpl`中，以下贴出关键代码：
```
public void generateAInterceptorInjectImpl(String pkName) {
        try {
            String name = pkName.replace(".",DECOLLATOR) + SUFFIX;
            logger.info(String.format("auto generate class = %s", name));
            TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Inject.class)
                    .addSuperinterface(AInterceptorInject.class);

            ClassName hashMap = ClassName.get("java.util", "HashMap");

            //Map<String, Class<?>>
            TypeName wildcard = WildcardTypeName.subtypeOf(Object.class);
            TypeName classOfAny = ParameterizedTypeName.get(ClassName.get(Class.class), wildcard);
            TypeName string = ClassName.get(Integer.class);

            TypeName map = ParameterizedTypeName.get(ClassName.get(Map.class), string, classOfAny);

            MethodSpec.Builder injectBuilder = MethodSpec.methodBuilder("getAInterceptors")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(map)
                    .addStatement("$T interceptorMap = new $T<>()", map, hashMap);

            for (Map.Entry<Integer, ClassName> entry : interceptorMap.entrySet()) {
                logger.info("add path= " + entry.getKey() + " and class= " + entry.getValue().simpleName());
                injectBuilder.addStatement("interceptorMap.put($L, $T.class)", entry.getKey(), entry.getValue());
            }
            injectBuilder.addStatement("return interceptorMap");

            builder.addMethod(injectBuilder.build());

            JavaFile javaFile = JavaFile.builder(pkName, builder.build())
                    .build();
            javaFile.writeTo(filer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

public void generateRouteInjectImpl(String pkName) {
        try {
            String name = pkName.replace(".",DECOLLATOR) + SUFFIX;
            logger.info(String.format("auto generate class = %s", name));
            TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Inject.class)
                    .addSuperinterface(RouteInject.class);

            ClassName hashMap = ClassName.get("java.util", "HashMap");

            //Map<String, String>
            TypeName wildcard = WildcardTypeName.subtypeOf(Object.class);
            TypeName classOfAny = ParameterizedTypeName.get(ClassName.get(Class.class), wildcard);
            TypeName string = ClassName.get(String.class);

            TypeName map = ParameterizedTypeName.get(ClassName.get(Map.class), string, classOfAny);

            MethodSpec.Builder injectBuilder = MethodSpec.methodBuilder("getRouteMap")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(map)
                    .addStatement("$T routMap = new $T<>()", map, hashMap);

            for (Map.Entry<String, ClassName> entry : routMap.entrySet()) {
                logger.info("add path= " + entry.getKey() + " and class= " + entry.getValue().enclosingClassName());
                injectBuilder.addStatement("routMap.put($S, $T.class)", entry.getKey(), entry.getValue());
            }
            injectBuilder.addStatement("return routMap");

            builder.addMethod(injectBuilder.build());

            JavaFile javaFile = JavaFile.builder(pkName, builder.build())
                    .build();
            javaFile.writeTo(filer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
```
## 二、Transform
Android Gradle 工具在 1.5.0 版本后提供了 Transfrom API, 允许第三方 Plugin在打包dex文件之前的编译过程中操作 .class 文件。这一部分面向高级Android工程师的，面向字节码编程，普通工程师可不做了解。

> 写到这里也许有人会有这样一个疑问，既然annotationProcessor这么好用为什么还有Transform面向字节码注入呢？这里需要解释以下，annotationProcessor具有局限性，annotationProcessor只能扫描当前module下的代码，切对于第三方的jar、aar文件都扫描不到。而Transform就没有这样的局限性，在打包dex文件之前的编译过程中操作.class 文件。

关于Transfrom API在Android Studio中如何使用可以参考[Transform API — a real world example](https://medium.com/grandcentrix/transform-api-a-real-world-example-cfd49990d3e1)，顺便提供一下[字节码指令](http://gityuan.com/2015/10/24/jvm-bytecode-grammar/)方便我们读懂ASM。

本项目中的Transform插件在`auto-inject`module中，实现源码`TransformPluginLaunch`如下，贴出关键部分：
```
/**
 *
 * 标准transform的格式，一般实现transform可以直接拷贝一份重命名即可
 *
 * 两处todo实现自己的字节码增强／优化操作
 */
class TransformPluginLaunch extends Transform implements Plugin<Project> {

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        //todo step1: 先扫描
        transformInvocation.inputs.each {
            TransformInput input ->
                input.jarInputs.each { JarInput jarInput ->
                   ...
                }

                input.directoryInputs.each { DirectoryInput directoryInput ->
                    //处理完输入文件之后，要把输出给下一个任务
                  ...
                }
        }

        //todo step2: ...完成代码注入
        if (InjectInfo.get().injectToClass != null) {
          ...
        }

    }

    /**
     * 扫描jar包
     * @param jarFile
     */
    static void scanJar(File jarFile, File destFile) {

    }

    /**
     * 扫描文件
     * @param file
     */
    static void scanFile(File file, File dest) {
       ...
    }
}
```
注入代码一般分为两个步骤
- 第一步：扫描
  这一部分主要是扫描的内容有：
   注入类和方法的信息，是AutoRegisterContract的实现类和其中@IMethod，@Inject的方法。
   待注入类的和方法信息，是RouteInject 和 AInterceptorInject实现类且被@Inject注解的。
- 第二步：注入
   以上扫描的结果，将待注入类注入到注入类的过程。这一过程面向ASM操作，可参考[字节码指令](http://gityuan.com/2015/10/24/jvm-bytecode-grammar/)来读懂以下的关键注入代码：
```
class InjectClassVisitor extends ClassVisitor {
...
    class InjectMethodAdapter extends MethodVisitor {

        InjectMethodAdapter(MethodVisitor mv) {
            super(Opcodes.ASM5, mv)
        }

        @Override
        void visitInsn(int opcode) {
            Log.e(TAG, "inject to class:")
            Log.e(TAG, own + "{")
            Log.e(TAG, "       public *** " + InjectInfo.get().injectToMethodName + "() {")
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                InjectInfo.get().injectClasses.each { injectClass ->
                    injectClass = injectClass.replace('/', '.')
                    Log.e(TAG, "           " + method + "(\"" + injectClass + "\")")
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                    mv.visitLdcInsn(injectClass)
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, own, method, "(Ljava/lang/String;)V", false)
                }
            }
            Log.e(TAG, "       }")
            Log.e(TAG, "}")
            super.visitInsn(opcode)
        }
...
    }
...
}
```
## 动态代理
定义：为其它对象提供一种代理以控制对这个对象的访问控制；在某些情况下，客户不想或者不能直接引用另一个对象，这时候代理对象可以在客户端和目标对象之间起到中介的作用。
Routerfit.register(Class<T> service) 这里就是采用动态代理的模式，使得ARetrofit的API非常简洁，使用者可以优雅定义出路由接口。关于动态代理的学习难度相对来说还比较小，想了解的同学可以参考这篇文章[java动态代理](https://www.ibm.com/developerworks/cn/java/j-lo-proxy1/index.html)。

本项目相关源码：
```
public final class Routerfit {
...
      private <T> T create(final Class<T> service) {
        RouterUtil.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                ServiceMethod<Object> serviceMethod = (ServiceMethod<Object>) loadServiceMethod(method, args);
                if (!TextUtils.isEmpty(serviceMethod.uristring)) {
                    Call<T> call = (Call<T>) new ActivityCall(serviceMethod);
                    return call.execute();
                }
                try {
                    if (serviceMethod.clazz == null) {
                        throw new RouteNotFoundException("There is no route match the path \"" + serviceMethod.routerPath + "\"");
                    }
                } catch (RouteNotFoundException e) {
                    Toast.makeText(ActivityLifecycleMonitor.getApp(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                if (RouterUtil.isSpecificClass(serviceMethod.clazz, Activity.class)) {
                    Call<T> call = (Call<T>) new ActivityCall(serviceMethod);
                    return call.execute();
                } else if (RouterUtil.isSpecificClass(serviceMethod.clazz, Fragment.class)
                        || RouterUtil.isSpecificClass(serviceMethod.clazz, android.app.Fragment.class)) {
                    Call<T> call = new FragmentCall(serviceMethod);
                    return call.execute();
                } else if (serviceMethod.clazz != null) {
                    Call<T> call = new IProviderCall<>(serviceMethod);
                    return call.execute();
                }

                if (serviceMethod.returnType != null) {
                    if (serviceMethod.returnType == Integer.TYPE) {
                        return -1;
                    } else if (serviceMethod.returnType == Boolean.TYPE) {
                        return false;
                    } else if (serviceMethod.returnType == Long.TYPE) {
                        return 0L;
                    } else if (serviceMethod.returnType == Double.TYPE) {
                        return 0.0d;
                    } else if (serviceMethod.returnType == Float.TYPE) {
                        return 0.0f;
                    } else if (serviceMethod.returnType == Void.TYPE) {
                        return null;
                    } else if (serviceMethod.returnType == Byte.TYPE) {
                        return (byte)0;
                    } else if (serviceMethod.returnType == Short.TYPE) {
                        return (short)0;
                    } else if (serviceMethod.returnType == Character.TYPE) {
                        return null;
                    }
                }
                return null;
            }
        });
    }
...
}
```
这里ServiceMethod是一个非常重要的类，使用了外观模式，主要用于解析方法中的被注解所有信息并保存起来。

## 拦截器链实现
本项目中的拦截器链设计，使得使用者可以非常优雅的处理业务逻辑。如下：
```
@Interceptor(priority = 3)
public class LoginInterceptor implements AInterceptor {

    private static final String TAG = "LoginInterceptor";
    @Override
    public void intercept(final Chain chain) {
        //Test2Activity 需要登录
        if ("/login-module/Test2Activity".equalsIgnoreCase(chain.path())) {
            Routerfit.register(RouteService.class).launchLoginActivity(new ActivityCallback() {
                @Override
                public void onActivityResult(int i, Object data) {
                    if (i == Routerfit.RESULT_OK) {//登录成功后继续执行
                        Toast.makeText(ActivityLifecycleMonitor.getTopActivityOrApp(), "登录成功", Toast.LENGTH_LONG).show();
                        chain.proceed();
                    } else {
                        Toast.makeText(ActivityLifecycleMonitor.getTopActivityOrApp(), "登录取消/失败", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            chain.proceed();
        }
    }

}
```
这一部分实现的思想是参考了okhttp中的拦截器，这里使用了java设计模式责任链模式，具体实现欢迎阅读源码。

# 结束语
到此，ARetrofit开源项目基本都以及讲完，在做这个项目的过程中其实遇到了各种各样的问题，其中ASM这块耗费的时间较长，对于当时的我还是个小白。当然收获也是颇多，这也是本人的第一个开源项目，存在的不足之处欢迎读者和用户提出，可以直接在qq群里提出。
