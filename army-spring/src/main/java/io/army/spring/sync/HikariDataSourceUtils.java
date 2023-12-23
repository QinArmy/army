package io.army.spring.sync;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.core.env.Environment;

import java.util.Properties;

public abstract class HikariDataSourceUtils extends DataSourceUtils {

    private HikariDataSourceUtils() {
        throw new UnsupportedOperationException();
    }


    public static HikariDataSource createDataSource(Environment env, Properties properties, final String tag, String role) {
        final HikariConfig config;
        config = createHikariConfig(env, properties, tag, role);
        return new HikariDataSource(config);
    }


    public static HikariConfig createHikariConfig(Environment env, Properties properties, final String tag, String role) {
        HikariConfig ds = new HikariConfig();

        final String url;
        url = putJdbcProperties(env, properties, tag, role);

        ds.setJdbcUrl(url);
        ds.setDataSourceProperties(properties);
        ds.setDriverClassName(getDriver(env, tag));
        ds.setConnectionTestQuery(env.getProperty(String.format("spring.datasource.%s.%s.connectionTestQuery", tag, role), "SELECT 1 "));

        ds.setKeepaliveTime(env.getProperty(String.format("spring.datasource.%s.%s.keepaliveTime", tag, role), Long.class, 0L));

        return ds;

    }


}
