package io.army.schema.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import org.springframework.core.env.Environment;

public abstract class DataSourceUtils {

    /**
     * 封装 数据源创建逻辑
     */
    public static DruidDataSource createDataSource(String schema,String username,String password) {
        DruidDataSource ds = new DruidDataSource();

        ds.setUrl("jdbc:mysql://localhost:3306/" + schema);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(Driver.class.getName());

        ds.setInitialSize(10);
        ds.setMaxActive(200);
        ds.setMaxWait(27L * 1000L);
        ds.setValidationQuery("SELECT NOW() FROM dual");

        ds.setTestOnBorrow( Boolean.FALSE);
        ds.setTestWhileIdle(Boolean.TRUE);
        ds.setTestOnReturn(Boolean.FALSE);
        ds.setTimeBetweenEvictionRunsMillis( 5L * 1000L);

        ds.setRemoveAbandoned(Boolean.FALSE);
        ds.setMinEvictableIdleTimeMillis(30000L);
        return ds;
    }
}
