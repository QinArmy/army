package io.army.boot;

import io.army.DataSourceUtils;
import io.army.SessionFactory;
import io.army.criteria.MetaException;
import io.army.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class BootstrapTests {

    private static final Logger LOG = LoggerFactory.getLogger(BootstrapTests.class);

    @Test
    public void boot() {
        SessionFactory sessionFactory = builder()
                .packagesToScan("com.example.domain.**")
                .build();

    }

    @Test//(expectedExceptions = {Throwable.class})
    public void bootstrapWithMultiInheritanceError() {
        SessionFactory sessionFactory = builder()
                .packagesToScan("com.example.error.inheritance.multi")
                .build();

    }


    public static SessionFactoryBuilder builder() {
        return SessionFactoryBuilder.builder()
                .datasource(DataSourceUtils.createDataSource("army", "army", "army123"))
                .readonly(false)
                .zoneId(TimeUtils.ZONE8)
                .showSql(true)
                .formatSql(true);
    }
}
