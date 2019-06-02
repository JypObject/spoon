package com.huadong.spoon.config;

import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

/**
 * @author jinjinhui
 * @date 2019/5/27
 */
@Component
@ImportResource(locations = {"classpath:spring-transaction.xml"})
public class TransationConfig {
}
