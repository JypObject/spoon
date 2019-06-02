package com.huadong.spoon.message;

import java.io.Serializable;

/**
 * @author jinjinhui
 * @date 2019/5/30
 */
public interface SpoonMessage extends Serializable{

    Short getVersion();
    void setVersion(Short version);
    Integer getMessageSequence();
    void setMessageSequence(Integer messageSequence);
    String getSender();
    void setSender(String sender);
    String getProductKey();
    void setProductKey(String productKey);
    String getDeviceSequence();
    void setDeviceSequence(String deviceSequence);
    Short getMessageType();
    void setMessageType(Short messageType);
    Short getMessageFlag();
    void setMessageFlag(Short messageFlag);
    byte[] getContentByte();
    void setContentByte(byte[] contentByte);
}
