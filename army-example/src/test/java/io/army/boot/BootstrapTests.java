package io.army.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class BootstrapTests {

    private static final Logger LOG = LoggerFactory.getLogger(BootstrapTests.class);

    @Test
    public void bootstrap() {
        final long startTime = System.currentTimeMillis();


       /* GenericSessionFactory sessionFactory = builder(map)
                .build();*/

        LOG.info("cost {} ms", System.currentTimeMillis() - startTime);
    }

    @Test//(expectedExceptions = {Throwable.class})
    public void bootstrapWithMultiInheritanceError() {
      /*  Map<String, Object> map = new HashMap<>();
        map.put(GenericSessionFactory.PACKAGE_TO_SCAN, "com.example.error.inheritance.multi");
        builder(map)
                .build();*/

    }

    @Test
    public void simple(){

    }


 /*  public static SessionFactoryBuilder builder(Map<String, Object> map) {
        final String hostIp = NetUtils.getPrivateAsString();

        StandardEnvironment env = new StandardEnvironment();
        env.addFirst("boot", map);

        SnowflakeClient snowflakeClient = new SingleApplicationSnowflakeClient(env);
        map.put(SnowflakeGenerator.SNOWFLAKE_CLIENT_NAME,snowflakeClient);
        map.put(SnowflakeGenerator.DEFAULT_START_TIME_KEY,"1580533269983");
        map.put(String.showSQL(SingleApplicationSnowflakeClient.DATA_CENTER_FORMAT,hostIp),"1");
        map.put(String.showSQL(SingleApplicationSnowflakeClient.WORKER_FORMAT,hostIp),"1");

        // init worker
        snowflakeClient.askAssignWorker();
        return  SessionFactoryBuilder.builder()
                .datasource(DataSourceUtils.createDataSource("army", "army", "army123"))
                .catalog("")
                .schema("")
                .environment(env)
                ;
    }*/
}
