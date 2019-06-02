package com.huadong.spoon;

import com.huadong.spoon.customer.CustomerManager;
import com.huadong.spoon.message.InnerSpoonMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author jinjinhui
 * @date 2019/5/30
 */
@Component
public class DataReceiver {

    @RabbitHandler
    public void received(InnerSpoonMessage message){
        Integer messageSeq = message.getMessageSequence();
        if(SpoonMessageSynchronizer.isSynchronized(String.valueOf(messageSeq))){
            //如果是同步消息，则放到同步队列中，以response的形式返回
            SpoonMessageSynchronizer.releaseLock(String.valueOf(messageSeq), message);
        }else{
            //如果不是同步消息，则直接以消息回调的形式返回
            CustomerManager.fireEvent(messageSeq, message);
        }
    }
}
