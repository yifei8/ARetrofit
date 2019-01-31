package com.sjtu.yifei.processor;

import com.sjtu.yifei.annotation.Inject;
import com.sjtu.yifei.ioc.AInterceptorInject;
import com.sjtu.yifei.utils.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/11
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class GenerateAInterceptorInjectImpl {

    private static final String SUFFIX = "$$AInterceptorInject";
    private static final String DECOLLATOR = "$$";
    private Logger logger;
    private Map<Integer, ClassName> interceptorMap;
    private Filer filer;

    public GenerateAInterceptorInjectImpl(Logger logger, Filer filer) {
        this.logger = logger;
        this.filer = filer;
        interceptorMap = new HashMap<>();
    }

    public void addInterceptorInjectMap(int priority, ClassName className) {
        interceptorMap.put(priority, className);
    }

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

}
