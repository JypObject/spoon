package com.huadong.spoon;

import com.huadong.spoon.message.SpoonMessage;
import com.rabbitmq.utility.BlockingCell;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author jinjinhui
 * @date 2019/5/30
 */
public class SpoonMessageSynchronizer {
    private static final Map<String, BlockingCell<Object>> syncMap = new ConcurrentHashMap<>();

    public static SpoonMessage putLock(String messageSeq, long waitTime){
        BlockingCell<Object> blockingCell = new BlockingCell<>();
        syncMap.put(messageSeq, blockingCell);
        try {
            Object response = blockingCell.get(waitTime);
            syncMap.remove(messageSeq);
            if(response == null){
                return null;
            }
            return (SpoonMessage) response;
        } catch (InterruptedException | TimeoutException e) {
            return null;
        }
    }

    public static boolean isSynchronized(String messageSeq){
        return syncMap.containsKey(messageSeq);
    }

    public static void releaseLock(String messageSeq, SpoonMessage data){
        if(syncMap.containsKey(messageSeq)){
            BlockingCell<Object> blockingCell = syncMap.get(messageSeq);
            blockingCell.set(data);
        }
    }
}
