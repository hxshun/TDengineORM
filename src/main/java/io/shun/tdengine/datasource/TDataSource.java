package io.shun.tdengine.datasource;

import io.shun.tdengine.entity.TDataSourceProperties;
import com.zaxxer.hikari.HikariConfig;

import javax.sql.DataSource;

/**
 * Created by shunZ on 2021/12/1 上午10:34
 */

public interface TDataSource {

    DataSource getDataSource(HikariConfig config);

    HikariConfig setProperties(TDataSourceProperties properties);
}
