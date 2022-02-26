package io.shun.tdengine.datasource;

import io.shun.tdengine.entity.TDataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @ClassName: TDataSourceConnectionFactory
 * @Description: 获取数据库连接对象的工程
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 15:27
 * @Version: 1.0
 **/

public class TDataSourceConnectionFactory implements TDataSourceConnection {

    private DataSource dataSource;

    @Autowired
    public TDataSourceConnectionFactory(TDataSourceProperties properties) {
        TDataSourceFactory tDataSourceFactory = new TDataSourceFactory();
        HikariConfig config = tDataSourceFactory.setProperties(properties);
        DataSource dataSource = tDataSourceFactory.getDataSource(config);
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void close(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
