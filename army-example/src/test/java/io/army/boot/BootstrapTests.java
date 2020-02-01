package io.army.boot;

import io.army.DataSourceUtils;
import io.army.SessionFactory;
import io.army.criteria.MetaException;
import io.army.env.StandardEnvironment;
import io.army.generator.snowflake.SingleApplicationSnowflakeClient;
import io.army.generator.snowflake.SnowflakeClient;
import io.army.generator.snowflake.SnowflakeGenerator;
import io.army.meta.TableMeta;
import io.army.util.NetUtils;
import io.army.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.net.InetAddress;
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

       LOG.info("cost {} ms",System.currentTimeMillis() - startTime);
    }

    @Test//(expectedExceptions = {Throwable.class})
    public void bootstrapWithMultiInheritanceError() {
        Map<String, Object> map = new HashMap<>();
        map.put(SessionFactory.PACKAGE_TO_SCAN, "com.example.error.inheritance.multi");
        builder(map)
                .build();

    }

    @Test
    public void simple(){

    }


    public static SessionFactoryBuilder builder(Map<String, Object> map) {
        final String hostIp = NetUtils.getPrivateAsString();

        StandardEnvironment env = new StandardEnvironment();
        env.addFirst("boot", map);

        SnowflakeClient snowflakeClient = new SingleApplicationSnowflakeClient(env);
        map.put(SnowflakeGenerator.SNOWFLAKE_CLIENT_NAME,snowflakeClient);
        map.put(SnowflakeGenerator.DEFAULT_START_TIME_KEY,"1580533269983");
        map.put(String.format(SingleApplicationSnowflakeClient.DATA_CENTER_FORMAT,hostIp),"1");
        map.put(String.format(SingleApplicationSnowflakeClient.WORKER_FORMAT,hostIp),"1");

        // init worker
        snowflakeClient.askAssignWorker();
        return SessionFactoryBuilder.builder()
                .datasource(DataSourceUtils.createDataSource("army", "army", "army123"))
                .catalog("")
                .schema("")
                .environment(env)
                ;
    }
}
