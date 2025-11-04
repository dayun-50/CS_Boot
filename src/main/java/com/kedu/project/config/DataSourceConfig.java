package com.kedu.project.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class DataSourceConfig {
    
    // ====================================================================
    // 1. Oracle DB 설정 (Primary)
    // ====================================================================
    @Primary
    @Bean(name = "oracleDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSource oracleDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "oracleSqlSessionFactory")
    public SqlSessionFactory oracleSqlSessionFactory(
            @Qualifier("oracleDataSource") DataSource oracleDataSource,
            ApplicationContext applicationContext) throws Exception {
        
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(oracleDataSource);
        
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        
        // Oracle 매퍼들
        String[] oracleMapperLocations = {
            "classpath:mappers/email/oracle_email-mapper.xml",
            "classpath:mappers/mamber/*.xml",          // 오타 수정
            "classpath:mappers/chatting/*.xml",
            "classpath:mappers/*.xml"
        };
        
        for (String location : oracleMapperLocations) {
            resources.addAll(Arrays.asList(resolver.getResources(location)));
        }
        
        factoryBean.setMapperLocations(resources.toArray(new Resource[0]));
        
        return factoryBean.getObject();
    }

    @Primary
    @Bean(name = "oracleSqlSessionTemplate")
    public SqlSessionTemplate oracleSqlSessionTemplate(
            @Qualifier("oracleSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // ====================================================================
    // 2. MySQL DB 설정 (Secondary: James)
    // ====================================================================
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(
            @Qualifier("mysqlDataSource") DataSource mysqlDataSource,
            ApplicationContext applicationContext) throws Exception {
        
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(mysqlDataSource);
        
        // MySQL 매퍼는 james_mailbox-mapper.xml만
        Resource[] mysqlMapperResources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mappers/email/james_mailbox-mapper.xml");
        
        factoryBean.setMapperLocations(mysqlMapperResources);
        
        return factoryBean.getObject();
    }

    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(
            @Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    // ====================================================================
    // 3. 트랜잭션 매니저 (Oracle용)
    // ====================================================================
    @Bean
    public DataSourceTransactionManager transactionManager(
            @Qualifier("oracleDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
