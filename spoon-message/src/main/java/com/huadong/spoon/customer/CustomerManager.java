package com.huadong.spoon.customer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huadong.spoon.message.InnerSpoonMessage;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jinjinhui
 * @date 2019/6/1
 */
public class CustomerManager {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerManager.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);
    private static Map<String, List<SpoonMessageCustomer>> messageMap = Maps.newConcurrentMap();

    /**
     * 注册事件监听
     * @param messageType
     * @param callback
     */
    public static void registerSpoonMessageCustomer(short messageType, SpoonMessageCustomer callback){
        registerSpoonMessageCustomer(messageType, callback, true);
    }

    /**
     * 多个消息类型同时使用一个回调函数
     * @param messageTypes
     * @param callback
     */
    public static void registerSpoonMessageCustomer(SpoonMessageCustomer callback, short... messageTypes){
        if(messageTypes == null || messageTypes.length == 0){
            return;
        }
        for(short messageType : messageTypes){
            registerSpoonMessageCustomer(messageType, callback);
        }
    }

    /**
     * 注册事件监听
     * 当isCover为false时，请注意及时调用unRegistListener方法
     * @param messageType
     * @param callback
     * @param isCover
     */
    public static void registerSpoonMessageCustomer(short messageType, SpoonMessageCustomer callback, Boolean isCover){
        if(messageType == 0 || callback == null){
            throw new IllegalArgumentException("MessageType or InnerMnsMessageCallback can not be null.");
        }
        if(isCover == null){
            isCover = true;
        }
        String sMsgType = "" + messageType;
        if(!messageMap.containsKey(sMsgType)){
            messageMap.put(sMsgType, Lists.newLinkedList());
        }
        List<SpoonMessageCustomer> registeredList = messageMap.get(sMsgType);
        if(isCover){
            for(SpoonMessageCustomer customer : registeredList){
                if(customer.getClass() == callback.getClass()){
                    registeredList.remove(customer);
                }
            }
        }
        registeredList.add(callback);
    }

    /**
     * 触发事件
     * @param messageType
     * @param message
     */
    public static void fireEvent(Integer messageType, InnerSpoonMessage message){
        if(messageType == null || message == null){
            throw new IllegalArgumentException("MessageType or message can not be null.");
        }
        String sMsgType = "" + messageType;
        List<SpoonMessageCustomer> customerList = messageMap.get(sMsgType);
        if(CollectionUtils.isEmpty(customerList)){
            LOG.info("No Customer found for message type {}", message.getMessageType());
            return;
        }
        for(SpoonMessageCustomer customer : customerList){
            //线程池触发
            EXECUTOR_SERVICE.execute(()->{
                try {
                    customer.messageArrived(message);
                }catch (Exception e){
                    LOG.error("Error happens when invoking SpoonMessage", e);
                }
            });
        }
    }

    /**
     * 注销某消息类型的所有监听
     * @param messageType
     */
    public static void unRegisterCustomer(Short messageType){
        messageMap.remove(""+messageType);
    }

    /**
     * 注销某个回调函数
     * @param messageType
     * @param callback
     */
    public static void unRegisterListener(Short messageType, SpoonMessageCustomer callback){
        String sMsgType = "" +messageType;
        if(!messageMap.containsKey(sMsgType)){
            return;
        }
        List<SpoonMessageCustomer> callbackList = messageMap.get(sMsgType);
        if(!callbackList.contains(callback)){
            return;
        }
        callbackList.remove(callback);
    }
}
