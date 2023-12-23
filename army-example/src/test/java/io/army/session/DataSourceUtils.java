package io.army.session;


import com.alibaba.druid.pool.DruidDataSource;

import java.util.Properties;


public abstract class DataSourceUtils {

    private DataSourceUtils() {
        throw new UnsupportedOperationException();
    }


    public static DruidDataSource druidDataSourceProps(DruidDataSource ds, String url, Properties properties) {


        ds.setUrl(url);
        ds.setDriverClassName(mapDriverName(url));
        ds.setConnectProperties(properties);

        ds.setInitialSize(10);
        ds.setMaxActive(200);
        ds.setMaxWait(27L * 1000L);
        ds.setValidationQuery("SELECT 1 ");

        ds.setTestOnBorrow(Boolean.FALSE);
        ds.setTestWhileIdle(Boolean.TRUE);
        ds.setTestOnReturn(Boolean.TRUE);
        ds.setTimeBetweenEvictionRunsMillis(5L * 1000L);

        ds.setRemoveAbandoned(Boolean.FALSE);
        ds.setMinEvictableIdleTimeMillis(30000L);
        return ds;
    }

    private static String mapDriverName(final String url) {
        final String driverName;
        if (url.startsWith("jdbc:mysql:")) {
            driverName = com.mysql.cj.jdbc.Driver.class.getName();
        } else if (url.startsWith("jdbc:postgresql:")) {
            driverName = org.postgresql.Driver.class.getName();
        } else {
            throw new IllegalArgumentException();
        }
        return driverName;
    }


}
