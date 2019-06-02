package com.huadong.spoon.mybatis.orderhelper;

/**
 * Created by jinjinhui on 2018/5/14.
 */
public class OrderDto {
    private String columnName;
    private String orderDir;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(String orderDir) {
        this.orderDir = orderDir;
    }
}