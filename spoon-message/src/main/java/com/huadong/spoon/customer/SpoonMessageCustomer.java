package com.huadong.spoon.customer;

import com.huadong.spoon.message.InnerSpoonMessage;

/**
 * @author jinjinhui
 * @date 2019/6/1
 */
public interface SpoonMessageCustomer {

    /**
     * 消息回调类
     * @param message
     */
    void messageArrived(InnerSpoonMessage message);
}
