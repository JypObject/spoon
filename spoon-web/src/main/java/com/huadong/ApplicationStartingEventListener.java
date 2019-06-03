package com.huadong;

import com.huadong.spoon.annotation.SpoonMessage;
import com.huadong.spoon.customer.CustomerRegistrar;
import com.huadong.spoon.utils.ServiceLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * @Auther: jinjinhui
 * @Date: 2019/6/3
 * @Description:
 */
public class ApplicationStartingEventListener implements ApplicationListener<ApplicationContextEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        ServiceLocator.setApplicationContext(event.getApplicationContext());
        CustomerRegistrar customerRegistrar = ServiceLocator.findService(CustomerRegistrar.class);
        customerRegistrar.doRegistry();
    }

}
