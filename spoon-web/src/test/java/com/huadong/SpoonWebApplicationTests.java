package com.huadong;

import com.alibaba.fastjson.JSON;
import com.huadong.spoon.DataReceiver;
import com.huadong.spoon.DataSender;
import com.huadong.spoon.QueueEnum;
import com.huadong.spoon.SpoonMessageSynchronizer;
import com.huadong.spoon.customer.CustomerManager;
import com.huadong.spoon.message.DefaultSpoonMessage;
import com.huadong.spoon.message.GpsMessageCustomer;
import com.huadong.spoon.message.InnerSpoonMessage;
import com.huadong.spoon.message.MessageFlag;
import com.huadong.spoon.message.MessageType;
import com.huadong.spoon.message.SpoonMessage;
import com.huadong.spoon.model.GpsData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpoonWebApplicationTests {

    @Autowired
	DataSender dataSender;

	@Test
	public void contextLoads() {}

	@Test
	public void testSendQueryMessageGpsData(){
        SpoonMessage message = new InnerSpoonMessage();
        message.setMessageSequence(123);
        message.setMessageType(MessageType.QUERY_GPS);
        message.setMessageFlag(MessageFlag.REQUEST);
        message.setSender(QueueEnum.QUEUE_GPS.getName());
        GpsData gpsData = new GpsData();
        gpsData.setLat(120.1);
        gpsData.setLng(30.5);
        message.setContentByte(JSON.toJSONBytes(gpsData));
        SpoonMessage response = dataSender.sendRequest(QueueEnum.QUEUE_TO_CMS, message, 10*1000);
        System.out.println("response = "+response);
    }

    @RabbitHandler
    @RabbitListener(queues = {"spoon.gps"})
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
