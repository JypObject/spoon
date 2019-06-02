package com.huadong.spoon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SpoonMessageCustomer扫描路径
 * @author jinjinhui
 * @date 2019/6/1
 * @see SpoonMessage
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpoonMessageScan {
    /**
     * SpoonMessage 扫描路径
     * {@code @SpoonMessageScan("org.my.pkg")} instead of {@code @SpoonMessageScan(basePackages = "org.my.pkg"})}.
     *
     * @return base package names
     */
    String[] value() default {};

    /**
     * SpoonMessage 扫描路径
     *
     * @return base package names for scanning SpoonMessage
     */
    String[] basePackages() default {};

    /**
     * SpoonMessage 实现类列表
     *
     * @return classes that indicate base package for scanning SpoonMessage
     */
    Class<?>[] basePackageClasses() default {};
}
