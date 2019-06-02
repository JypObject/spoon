package com.huadong.spoon;

import lombok.Getter;

/**
 * 基础知识：
 * RabbitMQ交换器类型（即分发消息的方式）有三类：fanout、direct、topic
 * fanout：不处理路由键，直接将消息转发到连接了该交换机的所有队列，类似于广播
 * direct：严格按照路由键来分发
 * topic：路由键允许使用通配符，"#"匹配一个或多个词，符号"*"匹配一个词
 * 这里的交换名称就是相当于定于了一个交换的规则，规则的类型就是这三类；
 * 队列名就是订阅的队列的名称；
 * 路由键就是消息转发的规则；
 * 消息队列枚举配置
 *
 * @author jinjinhui
 * @date 2019/5/9
 */
@Getter
public enum QueueEnum {
    /**
     * GPS消息队列
     */
    QUEUE_TO_CMS("spoon.toCms.direct", "spoon.toCms", "spoon.toCms"),
    QUEUE_GPS("spoon.gps.direct", "spoon.gps", "spoon.gps");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
