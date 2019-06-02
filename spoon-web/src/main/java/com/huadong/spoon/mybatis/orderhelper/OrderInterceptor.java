package com.huadong.spoon.mybatis.orderhelper;

import com.alibaba.druid.util.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * 排序拦截器
 * @author jinjinhui
 * @date 2018/5/7
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
)})
public class OrderInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(OrderInterceptor.class);

    /**
     * 拦截器的执行主方法
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            //1.判断是否需要排序
            OrderDto dto = OrderHelper.getOrderQueryDto();
            if(isOrderQueryDtoEmpty(dto)){
                return invocation.proceed();
            }
            //1.获取会话
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            ResultMap resultMap = mappedStatement.getResultMaps().get(0);
            //2.获取参数
            Object parameter = invocation.getArgs()[1];
            //3.获取原始sql
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String originalSql = boundSql.getSql().trim();

            //获取排序sql
            String orderSql = getOrderSql(originalSql, dto, resultMap);
            BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql, orderSql);

            MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
            invocation.getArgs()[0] = newMs;
            if(invocation.getArgs().length == 6){
                invocation.getArgs()[5] = newBoundSql;
            }
        }finally {
            OrderHelper.clearOrder();
        }
        return invocation.proceed();
    }

    /**
     * 校验该dto是否为空
     * @param dto
     * @return
     */
    private boolean isOrderQueryDtoEmpty(OrderDto dto) {
        if(dto == null){
            return true;
        }
        if(StringUtils.isEmpty(dto.getColumnName())){
            return true;
        }
        return false;
    }

    /**
     * 复制MappedStatement对象
     */
    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (null != ms.getKeyProperties()) {
            if (ms.getKeyProperties().length > 0) {
                builder.keyProperty(ms.getKeyProperties()[0]);
            }
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * 复制BoundSql对象
     */
    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(),
                boundSql.getParameterObject());
                for (ParameterMapping mapping : boundSql.getParameterMappings()) {
                    String prop = mapping.getProperty();
                    if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    /**
     * 获取排序sql
     * @param originalSql
     * @param dto
     * @param resultMap
     * @return
     */
    private String getOrderSql(String originalSql, OrderDto dto, ResultMap resultMap) {
        List<ResultMapping> resultMappings = resultMap.getPropertyResultMappings();
        String orderColumn = "";
        for(ResultMapping mapping : resultMappings){
            if(StringUtils.equals(mapping.getProperty(), dto.getColumnName())){
                orderColumn = mapping.getColumn();
                break;
            }
        }
        StringBuilder sBuilder = new StringBuilder("SELECT tod_.* FROM (" + originalSql + ") tod_");
        if(!StringUtils.isEmpty(orderColumn)){
            sBuilder.append(" order by tod_."+orderColumn+" "+dto.getOrderDir());
        }else{
            LOG.info("Can not get database column name by parameter name {}", new Object[]{orderColumn});
        }
        return sBuilder.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}

    /**
     * 内部类，用于统一保存boundSql
     */
    class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}