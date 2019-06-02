package com.huadong.spoon.message;

import java.util.Arrays;

/**
 * @author jinjinhui
 * @date 2019/5/30
 */
public class DefaultSpoonMessage implements SpoonMessage{

    protected Short version;
    protected Integer messageSequence;
    protected String sender;
    protected String productKey;
    protected String deviceSequence;
    protected Short messageType;
    protected Short messageFlag;
    protected byte[] contentByte;

    @Override
    public Short getVersion() {
        return version;
    }

    @Override
    public void setVersion(Short version) {
        this.version = version;
    }

    @Override
    public Integer getMessageSequence() {
        return messageSequence;
    }

    @Override
    public void setMessageSequence(Integer messageSequence) {
        this.messageSequence = messageSequence;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String getProductKey() {
        return productKey;
    }

    @Override
    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    @Override
    public String getDeviceSequence() {
        return deviceSequence;
    }

    @Override
    public void setDeviceSequence(String deviceSequence) {
        this.deviceSequence = deviceSequence;
    }

    @Override
    public Short getMessageType() {
        return messageType;
    }

    @Override
    public void setMessageType(Short messageType) {
        this.messageType = messageType;
    }

    @Override
    public Short getMessageFlag() {
        return messageFlag;
    }

    @Override
    public void setMessageFlag(Short messageFlag) {
        this.messageFlag = messageFlag;
    }

    @Override
    public byte[] getContentByte() {
        return contentByte;
    }

    @Override
    public void setContentByte(byte[] contentByte) {
        this.contentByte = contentByte;
    }

    @Override
    public String toString() {
        return "DefaultSpoonMessage{" +
                "version=" + version +
                ", messageSequence=" + messageSequence +
                ", sender='" + sender + '\'' +
                ", productKey='" + productKey + '\'' +
                ", deviceSequence='" + deviceSequence + '\'' +
                ", messageType=" + messageType +
                ", messageFlag=" + messageFlag +
                ", contentByte=" + Arrays.toString(contentByte) +
                '}';
    }
}
