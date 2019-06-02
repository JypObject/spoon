package com.huadong.spoon.mybatis.orderhelper;

import com.huadong.spoon.mybatis.dto.OrderQueryDto;
import com.huadong.spoon.utils.StringUtils;

/**
 * Created by jinjinhui on 2018/5/12.
 */
public class OrderHelper {
    private static final ThreadLocal<OrderDto> orderThreadLocal = new ThreadLocal<OrderDto>();

    private static final String ORDER_DIR_ASC = "asc";
    private static final String ORDER_DIR_DESC = "desc";

    /**
     * 重载排序工具初始方法
     * @param orderQueryDto
     */
    public static final void startOrder(OrderQueryDto orderQueryDto){
        if(orderQueryDto == null){
            throw new IllegalArgumentException("OrderQueryDto can not be null.");
        }
        OrderHelper.startOrder(orderQueryDto.getOrderColumn(), orderQueryDto.getOrderDir());
    }

    /**
     * 排序工具初始方法，需要排序时，调用本方法
     * @param columnName
     * @param orderDir
     */
    public static final void startOrder(String columnName, String orderDir){
        if(StringUtils.isBlank(orderDir)){
            //排除了null和空String的情况
            orderDir = ORDER_DIR_ASC;
        }
        if(!ORDER_DIR_ASC.equalsIgnoreCase(orderDir) && !ORDER_DIR_DESC.equalsIgnoreCase(orderDir)){
            throw new IllegalArgumentException("Illegal order direction argument.");
        }
        OrderDto dto = new OrderDto();
        dto.setColumnName(columnName);
        dto.setOrderDir(orderDir);
        setOrderQueryDto(dto);
    }

    private static void setOrderQueryDto(OrderDto dto){
        orderThreadLocal.set(dto);
    }

    public static OrderDto getOrderQueryDto(){
        return orderThreadLocal.get();
    }

    /**
     * 清除排序数据，防止保存上次排序的条件
     */
    public static void clearOrder(){
        orderThreadLocal.remove();
    }

}