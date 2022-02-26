package io.shun.tdengine.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @ClassName: Configuration
 * @Description:
 * @Author: ShunZ
 * @CreatedAt: 2021-11-30 16:28
 * @Version: 1.0
 **/

@Data
@Getter
@Setter
@NoArgsConstructor
public class TDataSourceProperties {

    private String driverClassName = "com.taosdata.jdbc.TSDBDriver";

    private String url = "jdbc:TAOS://:6030/test?charset=UTF-8&locale=en_US.UTF-8&timezone=UTC-8";

    private String database = "";

    private String username;

    private String password;

    private Integer minimumIdle = 1000;

    private Integer maximumPoolSize = 1000;

    private Integer connectionTimeout = 3000;

    private Integer maxLifetime = 0;

    private Integer idleTimeout = 0;

    private Boolean printSql = false;
}
