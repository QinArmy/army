package io.army.boot;

import io.army.DataSourceUtils;
import io.army.SessionFactory;
import io.army.criteria.MetaException;
import io.army.env.StandardEnvironment;
import io.army.meta.TableMeta;
import io.army.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class BootstrapTests {

    private static final Logger LOG = LoggerFactory.getLogger(BootstrapTests.class);

    @Test
    public void bootstrap() {
        final  long startTime = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put(SessionFactory.PACKAGE_TO_SCAN, "com.example.domain");

        SessionFactory sessionFactory = builder(map)
                .build();

        Map<Class<?>, TableMeta<?>> tableMetaMap = sessionFactory.tableMetaMap();
        for (Class<?> entityClass : tableMetaMap.keySet()) {
            LOG.info("entity:{}", entityClass.getName());
        }
       LOG.info("cost {} ms",System.currentTimeMillis() - startTime);
    }

    @Test//(expectedExceptions = {Throwable.class})
    public void bootstrapWithMultiInheritanceError() {
        Map<String, Object> map = new HashMap<>();
        map.put(SessionFactory.PACKAGE_TO_SCAN, "com.example.error.inheritance.multi");
        builder(map)
                .build();

    }


    public static SessionFactoryBuilder builder(Map<String, Object> map) {

        StandardEnvironment env = new StandardEnvironment();
        env.addFirst("boot", map);
        return SessionFactoryBuilder.builder()
                .datasource(DataSourceUtils.createDataSource("army", "army", "army123"))
                .catalog("")
                .schema("")
                .environment(env)
                ;
    }
}
