package com.huadong.spoon.config;

import com.huadong.spoon.DataReceiver;
import com.huadong.spoon.QueueEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * durable代表持久化
 * 消息队列配置
 *
 * @author jinjinhui
 * @date 2019/5/29
 * @see QueueEnum
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange gpsDirect() {
        return (DirectExchange) ExchangeBuilder
            .directExchange(QueueEnum.QUEUE_GPS.getExchange())
            .durable(true)
            .build();
    }

    /**
     * gps实际消费队列
     */
    @Bean
    public Queue gpsQueue() {
        return QueueBuilder.durable(QueueEnum.QUEUE_GPS.getName()).build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange gpsDirect, Queue gpsQueue){
        return BindingBuilder
            .bind(gpsQueue)
            .to(gpsDirect)
            .with(QueueEnum.QUEUE_GPS.getRouteKey());
    }

}
