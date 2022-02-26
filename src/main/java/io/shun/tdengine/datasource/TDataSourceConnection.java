package io.shun.tdengine.datasource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @ClassName: TDataSourceConnection
 * @Description:
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 15:27
 * @Version: 1.0
 **/

public interface TDataSourceConnection {

    Connection getConnection() throws SQLException;


    void close(Connection connection) throws SQLException;
}
