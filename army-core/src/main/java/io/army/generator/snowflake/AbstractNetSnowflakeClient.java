package io.army.generator.snowflake;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractNetSnowflakeClient extends AbstractSnowflakeClient {


    /*################################## blow static properties ##################################*/

    protected static final String EMAIL = "snowflake.client.email";

    protected static final String BASE_URL = "snowflake.server.base.url";

    protected static final String URI_FORMAT = "snowflake.server.%s.uri";


    protected static final String HEARTBEAT_RATE = "${snowflake.client.heartbeat.rate.expression}";

    protected static final String INIT_DELAY = "${snowflake.client.heartbeat.init.delay}";


    protected final AtomicReference<String> nodeIp = new AtomicReference<>(null);

    protected final AtomicReference<Integer> nodePid = new AtomicReference<>(null);

    protected final AtomicReference<String> nodeUser = new AtomicReference<>(null);

    protected final AtomicReference<String> nodeEnv = new AtomicReference<>(null);

    protected final AtomicBoolean loadBalanceEnable = new AtomicBoolean(false);

    protected final AtomicBoolean revoked = new AtomicBoolean(false);

    protected final AtomicReference<LocalDateTime> lastHeartbeatTime = new AtomicReference<>(null);

    protected final AtomicInteger workerUpdateCount = new AtomicInteger(0);


}
