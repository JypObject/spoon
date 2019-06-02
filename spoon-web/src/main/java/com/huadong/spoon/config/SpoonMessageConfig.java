package com.huadong.spoon.config;

import com.huadong.spoon.annotation.SpoonMessageScan;
import com.huadong.spoon.customer.CustomerRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author jinjinhui
 * @date 2019/6/1
 */
@Component
@SpoonMessageScan("com.huadong.spoon")
public class SpoonMessageConfig {

    @Bean
    public CustomerRegistrar customerRegistrar(){
        CustomerRegistrar registrar = new CustomerRegistrar(this.getClass());
        registrar.doRegistry();
        return registrar;
    }
}
