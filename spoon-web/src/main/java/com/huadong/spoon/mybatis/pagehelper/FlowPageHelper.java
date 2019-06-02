package com.huadong.spoon.mybatis.pagehelper;

/**
 * 流式分页主入口
 * Created by jinjinhui on 2018/5/11.
 */
public class FlowPageHelper {

    protected static final ThreadLocal<FlowPage> LOCAL_PAGE = new ThreadLocal<FlowPage>();
    private static final String DEFAULT_PRIMARY_KEY = "i_id";

    /**
     * 设置 Page 参数
     *
     * @param flowPage
     */
    private static void setLocalPage(FlowPage flowPage) {
        LOCAL_PAGE.set(flowPage);
    }

    public static FlowPage getFlowPage(){
        return LOCAL_PAGE.get();
    }

    public static <T> FlowPage<T> startFlowPage(Integer pageSize, Long lastId, String primaryKey){
        if( lastId == null || lastId < 0){
            lastId=0L;
        }
        if(pageSize == null || pageSize <= 0){
            pageSize=10;
        }
        FlowPage flowPage = new FlowPage();
        flowPage.setLastId(lastId);
        flowPage.setPageSize(pageSize);
        flowPage.setPrimaryKey(primaryKey);
        setLocalPage(flowPage);
        return flowPage;
    }

    public static <T> FlowPage<T> startFlowPage(Integer pageSize, Long lastId){
        return startFlowPage(pageSize, lastId, DEFAULT_PRIMARY_KEY);
    }

    public static void clearFlowPage(){
        LOCAL_PAGE.remove();
    }

}