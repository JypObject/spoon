package com.huadong.spoon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实现注解化，避免出现大量的
 * CustomerManager.registerSpoonMessageCustomer(MessageType, new SpoonMessage());
 * 之类的代码，而是直接在SpoonMessageCustomer实现类上添加该注解
 * @author jinjinhui
 * @date 2019/6/1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpoonMessage {
    short[] messageTypes();

    /**
     * 当前类是否为单例
     * @return
     */
    boolean isSingleton() default false;

}
