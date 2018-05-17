package com.sjtu.yifei.processor;

import com.google.auto.service.AutoService;
import com.sjtu.yifei.annotation.Route;
import com.sjtu.yifei.utils.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sjtu.yifei.utils.Consts.ANNOTATION_EXTRA;
import static com.sjtu.yifei.utils.Consts.ANNOTATION_GO;
import static com.sjtu.yifei.utils.Consts.ANNOTATION_ROUTE;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/10
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
//这是用来注册注解处理器要处理的源代码版本。
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//这个注解用来注册注解处理器要处理的注解类型。有效值为完全限定名（就是带所在包名和路径的类全名
@SupportedAnnotationTypes({ANNOTATION_ROUTE, ANNOTATION_GO, ANNOTATION_EXTRA})
//来注解这个处理器，可以自动生成配置信息
@AutoService(Processor.class)
public class IProcessor extends AbstractProcessor {


    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;       // File util, write class file into disk.
    /**
     * 日志相关的辅助类
     */
    private Logger logger;

    private Types typeUtils;
    /**
     * 元素相关的辅助类
     */
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();                  // Generate class.
        typeUtils = processingEnv.getTypeUtils();            // Get type utils.
        elementUtils = processingEnv.getElementUtils();      // Get class meta.
        logger = new Logger(processingEnv.getMessager());   // Package the log utils.
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
            try {
                if (routeElements != null && routeElements.size() > 0) {
                    logger.info(">>> Apt Processor start... <<<");
                    GenerateRouteInjectImpl generateRouteInject = new GenerateRouteInjectImpl(logger, mFiler);
                    for (Element element : routeElements) {
                        //1.获取包名
                        PackageElement packageElement = elementUtils.getPackageOf(element);
                        String pkName = packageElement.getQualifiedName().toString();

                        //2.获取包装类类型
                        TypeElement enclosingElement = (TypeElement) element;

                        String enclosingName = enclosingElement.getQualifiedName().toString();
                        logger.info(String.format("enclosindClass = %s", enclosingName));

                        //3.获取注解元数据
                        Route aptType = element.getAnnotation(Route.class);
                        String value = aptType.path();
                        logger.info(String.format("value = %s", value));

                        generateRouteInject.addRouteMap(value, enclosingName);
                    }
                    String autoGenerateClass = "com.sjtu.yifei." + RandomStringUtils.randomAlphabetic(10);
                    generateRouteInject.generateRouteInjectImpl(autoGenerateClass);
                    logger.info(">>> Apt Processor succeed <<<");
                    return true;
                }
            } catch (Exception e) {
                logger.error(e);
                logger.error(">>> --- Apt Processor failed <<<");
            }
            return true;
        }
        return false;
    }


}
