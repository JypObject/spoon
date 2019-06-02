package com.huadong.spoon.mybatis.pagehelper;

import java.util.List;

/**
 * Created by jinjinhui on 2018/5/11.
 */
public class FlowPage<T> {

    /**
     * 页面大小
     */
    private int pageSize;

    /**
     * 上次分页最后一条数据的id，用于计算本次分页的开始值
     */
    private Long lastId;

    /**
     * 总数
     */
    private long total;

    /**
     * 主键名称
     */
    private String primaryKey;

    /**
     * 数据
     */
    private List<T> list;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}