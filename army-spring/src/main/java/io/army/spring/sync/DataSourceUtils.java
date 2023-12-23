package io.army.spring.sync;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Properties;

abstract class DataSourceUtils {


    DataSourceUtils() {
        throw new UnsupportedOperationException();
    }


    static String putJdbcProperties(Environment env, Properties properties, final String tag, String role) {

        final String url;
        url = env.getRequiredProperty(String.format("spring.datasource.%s.%s.url", tag, role));

        properties.put("user", env.getRequiredProperty(String.format("spring.datasource.%s.%s.username", tag, role)));
        properties.put("password", env.getRequiredProperty(String.format("spring.datasource.%s.%s.password", tag, role)));

        if (url.startsWith("jdbc:mysql:") && !properties.containsKey("allowMultiQueries")) {
            properties.put("allowMultiQueries", env.getProperty(String.format("spring.datasource.%s.%s.allowMultiQueries", tag, role), Boolean.class, Boolean.FALSE));
        }
        return url;
    }

    static String getDriver(Environment env, String tag) {
        String driver;
        driver = env.getProperty(String.format("spring.datasource.%s.driver-class-name", tag));
        if (!StringUtils.hasText(driver)) {
            driver = env.getRequiredProperty("spring.datasource.driver-class-name");
        }
        return driver;
    }


}
