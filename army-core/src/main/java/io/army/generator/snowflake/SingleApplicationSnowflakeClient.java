package io.army.generator.snowflake;

import io.army.env.ArmyEnvironment;
import io.army.session.GenericSessionFactory;
import io.army.util.NetUtils;
import io.army.util._Assert;

import java.net.InetAddress;

import static io.army.ArmyKey.DATA_CENTER_FORMAT;
import static io.army.ArmyKey.WORKER_FORMAT;

public final class SingleApplicationSnowflakeClient extends AbstractSnowflakeClient {

    public static SingleApplicationSnowflakeClient build(GenericSessionFactory sessionFactory) {
        return new SingleApplicationSnowflakeClient(sessionFactory.environment());
    }

    private final ArmyEnvironment env;


    private SingleApplicationSnowflakeClient(ArmyEnvironment env) {
        this.env = env;
    }

    @Override
    protected void initImportanceParam() {

    }

    @Override
    public void askAssignWorker() throws SnowflakeWorkerException {
        InetAddress address = NetUtils.getPrivateIp4();
        _Assert.state(address != null, "no ipv4");
        Worker worker = new Worker(
                env.getRequiredProperty(String.format(DATA_CENTER_FORMAT, address.getHostAddress()), Long.class)
                , env.getRequiredProperty(String.format(WORKER_FORMAT, address.getHostAddress()), Long.class)
        );

        if (workerHolder.compareAndSet(null, worker)) {
            LOG.info("init worker accomplished:{}", worker);
        } else {
            LOG.info("worker set by other thread");
        }

    }

    @Override
    public void heartbeat() throws SnowflakeWorkerException {
        //no-op
    }

}
