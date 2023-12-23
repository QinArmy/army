package io.army.spring.sync;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

abstract class DataSourceUtils {


    DataSourceUtils() {
        throw new UnsupportedOperationException();
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
