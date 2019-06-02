package com.huadong;

import com.alibaba.fastjson.JSON;
import com.huadong.spoon.DataSender;
import com.huadong.spoon.QueueEnum;
import com.huadong.spoon.message.DefaultSpoonMessage;
import com.huadong.spoon.message.GpsMessageCustomer;
import com.huadong.spoon.message.SpoonMessage;
import com.huadong.spoon.model.GpsData;
import org.junit.Test;
import org.junit.runner.RunWith;
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
	public void testSendQueryGpsData(){
        SpoonMessage message = new DefaultSpoonMessage();
        message.setMessageSequence(123);
        message.setMessageType((short) 123);
        message.setSender(QueueEnum.QUEUE_TO_CMS.getName());
		SpoonMessage response = dataSender.sendRequest(QueueEnum.QUEUE_GPS, message, 10*1000);
		GpsData gpsData = JSON.parseObject(response.getContentByte(), GpsData.class);
        System.out.println("SpoonWebApplicationTests#testSendGpsData receive data = "+gpsData);
    }

}
