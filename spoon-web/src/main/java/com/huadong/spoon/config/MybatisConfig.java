package com.huadong.spoon.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Mybatis配置类
 * @author jinjinhui
 * @date 2019/5/27
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.huadong.spoon.mapper","com..huadong.spoon.dao"})
public class MybatisConfig {
}
