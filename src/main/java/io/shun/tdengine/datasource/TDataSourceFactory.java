package io.shun.tdengine.datasource;

import io.shun.tdengine.constants.ExceptionMessage;
import io.shun.tdengine.exception.TDataSourceException;
import io.shun.tdengine.entity.TDataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @ClassName: TDataSourceFactory
 * @Description: DataSource工厂 用于生成DataSource对象
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 14:22
 * @Version: 1.0
 **/

public class TDataSourceFactory implements TDataSource {

    @Override
    public HikariConfig setProperties(TDataSourceProperties properties) {
        HikariConfig config = new HikariConfig();
        try{
            // 检查驱动
            Class.forName(properties.getDriverClassName());
            // jdbc properties
            config.setDriverClassName(properties.getDriverClassName());
            config.setJdbcUrl(properties.getUrl());

            if (properties.getUsername() == null) {
                throw new TDataSourceException(ExceptionMessage.USERNAME_NOT_NULL);
            }
            config.setUsername(properties.getUsername());

            if (properties.getPassword() == null) {
                throw new TDataSourceException(ExceptionMessage.PASSWORD_NOT_NULL);
            }
            config.setPassword(properties.getPassword());
            // pool configurations
            //minimum number of idle connection
            config.setMinimumIdle(properties.getMinimumIdle());
            //maximum number of connection in the pool
            config.setMaximumPoolSize(properties.getMaximumPoolSize());
            //maximum wait milliseconds for get connection from pool
            config.setConnectionTimeout(properties.getConnectionTimeout());
            // maximum life time for each connection
            config.setMaxLifetime(properties.getMaxLifetime());
            // max idle time for recycle idle connection
            config.setIdleTimeout(properties.getIdleTimeout());
            //validation query
            config.setConnectionTestQuery("select server_status()");

        }catch (ClassNotFoundException e) {
            throw new TDataSourceException(ExceptionMessage.DRIVER_NOT_FOUNT);
        }

        return config;
    }

    @Override
    public DataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }

}
