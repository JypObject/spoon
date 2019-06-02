package com.huadong.spoon.customer;


import com.huadong.spoon.annotation.SpoonMessageScan;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jinjinhui
 * @date 2019/3/7
 */
public class CustomerRegistrar {

    private Class<?> annotatedClass;

    public CustomerRegistrar(Class<?> annotatedClass){
        this.annotatedClass = annotatedClass;
    }

    public void doRegistry(){
        if(annotatedClass == null || !annotatedClass.isAnnotationPresent(SpoonMessageScan.class)){
            throw new RuntimeException("target class is not annotated by SpoonMessageScan");
        }
        SpoonMessageScan spoonMessageScan = annotatedClass.getAnnotation(SpoonMessageScan.class);
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(
                Arrays.stream(spoonMessageScan.value())
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList()));

        basePackages.addAll(
                Arrays.stream(spoonMessageScan.basePackages())
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList()));

        basePackages.addAll(
                Arrays.stream(spoonMessageScan.basePackageClasses())
                        .map(ClassUtils::getPackageName)
                        .collect(Collectors.toList()));

        CustomerScanner.doScan(basePackages);
    }

}
