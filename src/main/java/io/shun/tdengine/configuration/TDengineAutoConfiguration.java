package io.shun.tdengine.configuration;

import io.shun.tdengine.datasource.TDataSourceConnection;
import io.shun.tdengine.datasource.TDataSourceConnectionFactory;
import io.shun.tdengine.entity.TDataSourceProperties;
import io.shun.tdengine.processor.TDengineGenerateStatement;
import io.shun.tdengine.processor.TDengineGenerateStatementImpl;
import io.shun.tdengine.processor.TDengineModel;
import io.shun.tdengine.processor.TDengineModelImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: TDConfiguration
 * @Description: 自动配置类
 * @Author: ShunZ
 * @CreatedAt: 2021-11-30 17:44
 * @Version: 1.0
 **/
@Configuration
public class TDengineAutoConfiguration {

    @Bean
    @ConfigurationProperties("tdengine")
    public TDataSourceProperties getTDataSource() {
        return new TDataSourceProperties();
    }

    @Bean
    public TDataSourceConnection registryTDataSourceConnection() {
        TDataSourceProperties properties = getTDataSource();
        return new TDataSourceConnectionFactory(properties);
    }

    @Bean
    public TDengineModel registryTDengineModel() {
        return new TDengineModelImpl();
    }

    @Bean
    public TDengineGenerateStatement registryTDengineGenerateStatement() {
        return new TDengineGenerateStatementImpl();
    }
}
