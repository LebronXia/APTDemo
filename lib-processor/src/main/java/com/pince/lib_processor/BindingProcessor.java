package com.pince.lib_processor;

import com.pince.lib_annotations.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Filter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by zxb in 2021/11/19
 */
public class BindingProcessor extends AbstractProcessor {

    Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element: roundEnv.getRootElements()){
            String packageStr = element.getEnclosingElement().toString();
            String classStr = element.getSimpleName().toString();

            ClassName className = ClassName.get(packageStr, classStr + "Binding");
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(packageStr, classStr), "activity");
            boolean hasBinding = false;
  //getEnclosingElement  单数 被抱住
            //子类里的元素  字段 方法
            for (Element enclosedElement: element.getEnclosedElements()){
                if (enclosedElement.getKind() == ElementKind.FIELD){
                    //寻找BindView注释
                    BindView bindView = enclosedElement.getAnnotation(BindView.class);
                    if (bindView != null){
                        hasBinding = true;
                        constructorBuilder.addStatement("activity.$N = activity.findViewById($L)",
                                enclosedElement.getSimpleName(), bindView.value());
                    }
                }
            }

            TypeSpec builtClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(constructorBuilder.build())
                    .build();

            if (hasBinding){
                try {
                    JavaFile.builder(packageStr, builtClass)
                            .build().writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        ClassName className = ClassName.get("com.pince.aptdemo", "MainActivityBinding");
//        TypeSpec builtClass = TypeSpec.classBuilder(className)
//                //添加类范围
//                .addModifiers(Modifier.PUBLIC)
//                .addMethod(MethodSpec.constructorBuilder()
//                        //添加方法类型
//                        .addModifiers(Modifier.PUBLIC)
//                        //添加方法参数
//                        .addParameter(ClassName.get("com.pince.aptdemo", "MainActivity"), "activity")
//                        //添加方法内容 不带有返回行的语句
//                        .addStatement("activity.textView = activity.findViewById(R.id.textView)")
//                        .build())
//                .build();
//
//        try {
//            JavaFile.builder("com.pince.aptdemo", builtClass)
//                    .build()
//                    .writeTo(filer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//       System.out.println("配置成功  Annataion Process start~~~~~~~");
//        try {
//            ClassName className = ClassName.get("com.pince.aptdemo", "Test");
//            TypeSpec builtClass = TypeSpec.classBuilder(className).build();
//            JavaFile.builder("com.pince.aptdemo", builtClass)
//                    .build()
//                    .writeTo(filer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //对着个注解进行注解处理
        return Collections.singleton(BindView.class.getCanonicalName());
    }
}
