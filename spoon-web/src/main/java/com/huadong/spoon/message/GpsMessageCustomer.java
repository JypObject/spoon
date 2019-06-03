package com.huadong.spoon.message;

import com.alibaba.fastjson.JSON;
import com.huadong.spoon.DataSender;
import com.huadong.spoon.annotation.SpoonMessage;
import com.huadong.spoon.customer.SpoonMessageCustomer;
import com.huadong.spoon.model.GpsData;
import com.huadong.spoon.utils.ServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * gps消息消费者
 * @author jinjinhui
 * @date 2019/6/1
 */
@SpoonMessage(messageTypes = {MessageType.QUERY_GPS})
public class GpsMessageCustomer implements SpoonMessageCustomer {

    private DataSender dataSender = ServiceLocator.findService(DataSender.class);

    @Override
    public void messageArrived(InnerSpoonMessage message) {
        GpsData gpsData = JSON.parseObject(message.getContentByte(), GpsData.class);
        System.out.println("gpsData = ["+JSON.toJSONString(gpsData)+"]");
        InnerSpoonMessage spoonMessage = new InnerSpoonMessage();
        spoonMessage.setMessageSequence(message.getMessageSequence());
        spoonMessage.setMessageFlag(MessageFlag.RESPONSE_SUCC);
        dataSender.sendMessage(message.getSender(), spoonMessage);
    }
}
