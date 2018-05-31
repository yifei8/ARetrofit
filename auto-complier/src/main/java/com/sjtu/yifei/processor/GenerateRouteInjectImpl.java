package com.sjtu.yifei.processor;

import com.sjtu.yifei.annotation.Inject;
import com.sjtu.yifei.ioc.RouteInject;
import com.sjtu.yifei.utils.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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

public class GenerateRouteInjectImpl {

    private static final String SUFFIX = "$$RouteInject";
    private static final String DECOLLATOR = "$$";
    private Logger logger;
    private Map<String, String> routMap;
    private Filer filer;

    public GenerateRouteInjectImpl(Logger logger, Filer filer) {
        this.logger = logger;
        this.filer = filer;
        routMap = new HashMap<>();
    }

    public void addRouteMap(String path, String className) {
        routMap.put(path, className);
    }

    /**
     * generate class like below
     *
     * @Inject
     * public final xxxx$$RouteInject implements RouteInject {
     *
     *      @Override
     *      public Map<String, Class<?>> getRouteMap() {
     *          Map<String, String> routMap = new HashMap<>();
     *          routMap.add("", Class<?>);
     *          ...
     *          return routMap;
     *      }
     *
     * }
     *
     */
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
            TypeName map = ParameterizedTypeName.get(Map.class, String.class, String.class);

            MethodSpec.Builder injectBuilder = MethodSpec.methodBuilder("getRouteMap")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(map)
                    .addStatement("$T routMap = new $T<>()", map, hashMap);

            for (Map.Entry<String, String> entry : routMap.entrySet()) {
                logger.info("add path= " + entry.getKey() + " and class= " + entry.getValue());
                injectBuilder.addStatement("routMap.put($S, $S)", entry.getKey(), entry.getValue());
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

}
