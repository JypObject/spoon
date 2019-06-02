package com.huadong.spoon.message;

import com.huadong.spoon.annotation.SpoonMessage;
import com.huadong.spoon.customer.SpoonMessageCustomer;

/**
 * @author jinjinhui
 * @date 2019/6/1
 */
@SpoonMessage(messageTypes = {123})
public class GpsMessageCustomer implements SpoonMessageCustomer {

    @Override
    public void messageArrived(InnerSpoonMessage message) {
        System.out.println("message = [" + message + "]");
    }
}
