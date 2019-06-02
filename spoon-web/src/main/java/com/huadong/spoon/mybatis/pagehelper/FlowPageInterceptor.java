package com.huadong.spoon.mybatis.pagehelper;

import com.google.common.collect.Lists;
import com.huadong.spoon.utils.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 流式分页拦截器
 * 需要了解的点：
 * 1.拦截器的执行顺序、与filter.dochain()模式相似，但是这两者的区别要能了解；
 * 2.invocation.getArgs()中各参数的意义，；
 * 3.每个mappedStatement都相当于是一个<select><update><insert><delete>标签，所以不能重复使用
 * 4.根据lastId获取到rownum，再由rownum获取起始位置
 * 5.使用ThreadLocal来保存分页信息，使用完之后及时删除，保证每次需要使用时，使用的都是本次最新的参数
 *
 * @author jinjinhui
 * @date 2018/5/11
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
public class FlowPageInterceptor implements Interceptor {

    private String flowCountSuffix = "_FLOWCOUNT";
    private String rowNumSuffix = "_ROW_NUM";
    private static final List<ResultMapping> EMPTY_RESULTMAPPING = Lists.newArrayListWithCapacity(0);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            FlowPage<?> flowPage = FlowPageHelper.getFlowPage();
            if(isFlowPageEmpty(flowPage)){
                return invocation.proceed();
            }
            Object[] args = invocation.getArgs();
            //1.获取会话
            MappedStatement mappedStatement = (MappedStatement) args[0];
            //2.获取参数
            Object parameter = args[1];
            Executor executor = (Executor) invocation.getTarget();
            ResultHandler resultHandler = (ResultHandler) args[3];
            //3.获取原始sql
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String originalSql = boundSql.getSql().trim();
            //4.组装获取count的sql
            String countSql = createCountSql(originalSql);
            BoundSql countBoundSql = copyFromBoundSql(mappedStatement, boundSql, countSql);
            MappedStatement countMappedStatement = createCountMappedStatement(mappedStatement, new BoundSqlSqlSource(countBoundSql), flowCountSuffix);
            Long total = executeGetLong(executor, countMappedStatement, countBoundSql, parameter, resultHandler);
            flowPage.setTotal(total);
            //5.组装获取rowNum的sql
            Long rowNum = null;
            if(!flowPage.getLastId().equals(0L)){
                String rowNumSql = createRowNumSql(originalSql, flowPage.getLastId(), flowPage.getPrimaryKey());
                BoundSql rowBoundSql = copyFromBoundSql(mappedStatement, boundSql, rowNumSql);
                MappedStatement rowNumMappedStatement = createCountMappedStatement(mappedStatement, new BoundSqlSqlSource(rowBoundSql), rowNumSuffix);
                rowNum = executeGetLong(executor, rowNumMappedStatement, rowBoundSql, parameter, resultHandler);
                if(rowNum == null){
                    rowNum = total;
                }
            }else{
                //lastId 为0,认为是第一次开始查询
                rowNum = 0L;
            }
            //6.计算新的lastId
            String newLastIdSql = createNewLastIdSql(originalSql, rowNum, flowPage.getPageSize(), flowPage.getPrimaryKey(), total);
            BoundSql rowBoundSql = copyFromBoundSql(mappedStatement, boundSql, newLastIdSql);
            MappedStatement rowNumMappedStatement = createCountMappedStatement(mappedStatement, new BoundSqlSqlSource(rowBoundSql), rowNumSuffix);
            Long newLastId = executeGetLong(executor, rowNumMappedStatement, rowBoundSql, parameter, resultHandler);
            flowPage.setLastId(newLastId);

            //7.组装获取数据的sql
            String pageSql = createPageSql(originalSql, rowNum, flowPage.getPageSize());
            BoundSql pageBoundSql = copyFromBoundSql(mappedStatement, boundSql, pageSql);
            MappedStatement pageMappedStatement = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(pageBoundSql));
            invocation.getArgs()[0] = pageMappedStatement;
            if(args.length == 6){
                invocation.getArgs()[5] = pageBoundSql;
            }

        }finally {
            //清理流式分页数据，避免下一次还是以本次的条件获取数据
            FlowPageHelper.clearFlowPage();
        }
        return invocation.proceed();
    }

    /**
     * 执行获取long的查询
     * @param executor
     * @param mappedStatement
     * @param boundSql
     *@param parameter
     * @param resultHandler   @return
     */
    private Long executeGetLong(Executor executor, MappedStatement mappedStatement, BoundSql boundSql, Object parameter, ResultHandler resultHandler) throws SQLException {
        CacheKey cacheKey = executor.createCacheKey(mappedStatement, parameter, RowBounds.DEFAULT, boundSql);
        List<?> resultList = executor.query(mappedStatement, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
        if(CollectionUtils.isEmpty(resultList)){
            return null;
        }
        Long result = Long.valueOf(((Number)(resultList).get(0)).longValue());
        return result;
    }

    /**
     * 创建count的mappedStatement
     * @param ms
     * @param boundSqlSqlSource
     * @return
     */
    private MappedStatement createCountMappedStatement(MappedStatement ms, BoundSqlSqlSource boundSqlSqlSource, String suffix) {
        String newMsId = ms.getId() + suffix;
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId, ms.getSqlSource(), SqlCommandType.SELECT);
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if(ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            String[] var4 = ms.getKeyProperties();
            int var5 = var4.length;
            for(int var6 = 0; var6 < var5; ++var6) {
                String keyProperty = var4[var6];
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        List<ResultMap> resultMaps = new ArrayList();
        ResultMap resultMap = (new org.apache.ibatis.mapping.ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, EMPTY_RESULTMAPPING)).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
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
     * 复制MappedStatement对象
     */
    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

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
     * 校验flowPage是否为空
     * @param flowPage
     * @return
     */
    private boolean isFlowPageEmpty(FlowPage flowPage) {
        if(flowPage == null){
            return true;
        }
        if(flowPage.getLastId() == null){
            return true;
        }
        return false;
    }

    /**
     * 组建获取数量的sql
     * @param originalSql
     * @return
     */
    private String createCountSql(String originalSql) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("select count(1) from (").append(originalSql).append(") ctt_");
        return sBuilder.toString();
    }

    private String createPageSql(String originalSql, Long rowNum, int pageSize) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("SELECT tb2_.* FROM ")
                .append(" (")
                .append("  SELECT (@rownum :=@rownum + 1) AS rownum, tb1_.* FROM ")
                .append("   (").append(originalSql).append(") tb1_, ")
                .append("   (SELECT @rownum := 0) t ")
                .append(" ) tb2_ ")
                .append("WHERE ")
                .append("tb2_.rownum > ")
                .append(rowNum)
                .append(" limit ")
                .append(pageSize);
        return sBuilder.toString();
    }

    /**
     * 创建获取rownum语句的sql
     SELECT tb2_.* FROM
     (
        SELECT @rownum :=@rownum + 1 AS rownum, tb1_.*
        FROM ( 目标sql ) tb1_,
             (SELECT @rownum := 0) t
     ) tb2_
     WHERE tb2_.i_id = #{lastId};
     *
     * @param originalSql
     * @param lastId
     * @return
     */
    private String createRowNumSql(String originalSql, Long lastId, String primaryKey) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("SELECT tb2_.* FROM ")
                .append("   ( ")
                .append("       SELECT (@rownum :=@rownum + 1) AS rownum, tb1_.* FROM ")
                .append("           ( ").append(originalSql).append(") tb1_, ")
                .append("           (SELECT @rownum := 0) t ")
                .append("   ) tb2_ ")
                .append("WHERE tb2_.").append(primaryKey).append(" = ")
                .append(lastId);
        return sBuilder.toString();
    }

    /**
     * 创建获取新的lastId的sql
     * @param originalSql
     * @param rowNum
     * @param pageSize
     * @param primaryKey
     * @param total
     * @return
     */
    private String createNewLastIdSql(String originalSql, Long rowNum, int pageSize, String primaryKey, Long total) {
        StringBuilder sBuilder = new StringBuilder();
        Long lastRowNum = rowNum + pageSize;
        lastRowNum = total<lastRowNum?total:lastRowNum;
        sBuilder.append("SELECT tb2_.").append(primaryKey).append(" FROM ")
                .append("   ( ")
                .append("       SELECT (@rownum :=@rownum + 1) AS rownum, tb1_.* FROM ")
                .append("           ( ").append(originalSql).append(") tb1_, ")
                .append("           (SELECT @rownum := 0) t ")
                .append("   ) tb2_ ")
                .append("WHERE tb2_.rownum = ")
                .append(lastRowNum);
        return sBuilder.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String countSuffix = properties.getProperty("countSuffix");
        if(StringUtils.isNotBlank(countSuffix)) {
            this.flowCountSuffix = countSuffix;
        }
    }

    class BoundSqlSqlSource implements SqlSource {

        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql){
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return this.boundSql;
        }
    }
}