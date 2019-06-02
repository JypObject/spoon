package com.huadong.spoon;

import com.huadong.spoon.message.SpoonMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jinjinhui
 * @date 2019/5/30
 */
@Component
public class DataSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(QueueEnum queue, SpoonMessage data){
        sendMessage(queue.getExchange(), queue.getRouteKey(), data);
    }

    public void sendMessage(String routeKey, SpoonMessage data){
        amqpTemplate.convertAndSend(routeKey, data);
    }

    /**
     * 发送异步消息
     * @param exchange
     * @param routeKey
     * @param data
     */
    public void sendMessage(String exchange, String routeKey, SpoonMessage data){
        amqpTemplate.convertAndSend(exchange, routeKey, data);
    }

    /**
     * 延时发送异步消息
     * @param exchange
     * @param routeKey
     * @param data
     * @param delayTimes
     */
    public void sendDelayMessage(String exchange, String routeKey, SpoonMessage data, long delayTimes){
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(exchange, routeKey, data, message -> {
            //给消息设置延迟毫秒值
            message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
            return message;
        });
    }

    /**
     * 发送同步消息
     * @param exchange
     * @param routeKey
     * @param data
     * @return
     */
    public SpoonMessage sendRequest(String exchange, String routeKey, SpoonMessage data, long waitTime){
        sendMessage(exchange, routeKey, data);
        Integer messageSequence = data.getMessageSequence();
        return SpoonMessageSynchronizer.putLock(String.valueOf(messageSequence), waitTime);
    }

    /**
     * 发送同步消息
     * @param queue
     * @param data
     * @param waitTime
     * @return
     */
    public SpoonMessage sendRequest(QueueEnum queue, SpoonMessage data, long waitTime){
        return sendRequest(queue.getExchange(), queue.getRouteKey(), data, waitTime);
    }

}
