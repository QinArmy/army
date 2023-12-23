package io.army.spring.sync;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.core.env.Environment;

import java.util.Properties;

public abstract class DruidDataSourceUtils extends DataSourceUtils {

    private DruidDataSourceUtils() {
        throw new UnsupportedOperationException();
    }


    /**
     * create {@link DruidDataSource}
     */
    public static DruidDataSource createDataSource(Environment env, Properties properties, final String tag, String role) {
        DruidDataSource ds = new DruidDataSource();
        setDataSourceProperties(ds, env, properties, tag, role);
        return ds;
    }


    public static void setDataSourceProperties(DruidDataSource ds, Environment env, Properties properties, final String tag, String role) {

        ds.setUrl(env.getRequiredProperty(String.format("spring.datasource.%s.%s.url", tag, role)));

        properties.put("user", env.getRequiredProperty(String.format("spring.datasource.%s.%s.username", tag, role)));
        properties.put("password", env.getRequiredProperty(String.format("spring.datasource.%s.%s.password", tag, role)));

        ds.setConnectProperties(properties);
        ds.setDriverClassName(getDriver(env, tag));

        ds.setInitialSize(env.getProperty(String.format("spring.datasource.%s.%s.initialSize", tag, role), Integer.class, 10));
        ds.setMaxActive(env.getProperty(String.format("spring.datasource.%s.%s.maxActive", tag, role), Integer.class, 200));
        ds.setMaxWait(env.getProperty(String.format("spring.datasource.%s.%s.maxWait", tag, role), Long.class, 27L * 1000L));
        ds.setValidationQuery(env.getProperty(String.format("spring.datasource.%s.%s.validationQuery", tag, role), "SELECT 1 "));

        ds.setTestOnBorrow(env.getProperty(String.format("spring.datasource.%s.%s.testOnBorrow", tag, role), Boolean.class, Boolean.FALSE));
        ds.setTestWhileIdle(env.getProperty(String.format("spring.datasource.%s.%s.testWhileIdle", tag, role), Boolean.class, Boolean.TRUE));
        ds.setTestOnReturn(env.getProperty(String.format("spring.datasource.%s.%s.testOnReturn", tag, role), Boolean.class, Boolean.FALSE));
        ds.setTimeBetweenEvictionRunsMillis(env.getProperty(String.format("spring.datasource.%s.%s.timeBetweenEvictionRunsMillis", tag, role), Long.class, 5L * 1000L));

        ds.setRemoveAbandoned(env.getProperty(String.format("spring.datasource.%s.%s.removeAbandoned", tag, role), Boolean.class, Boolean.FALSE));
        ds.setMinEvictableIdleTimeMillis(env.getProperty(String.format("spring.datasource.%s.%s.minEvictableIdleTimeMillis", tag, role), Long.class, 30000L));
    }


}
