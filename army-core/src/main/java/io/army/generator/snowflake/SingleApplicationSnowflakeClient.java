package io.army.generator.snowflake;

import io.army.env.Environment;
import io.army.util.Assert;
import io.army.util.NetUtils;

import java.net.InetAddress;

import static io.army.ArmyConfigConstant.DATA_CENTER_FORMAT;
import static io.army.ArmyConfigConstant.WORKER_FORMAT;

public final class SingleApplicationSnowflakeClient extends AbstractSnowflakeClient {


    public static SingleApplicationSnowflakeClient build(Environment env) {
        return new SingleApplicationSnowflakeClient(env);
    }

    private final Environment env;

    private SingleApplicationSnowflakeClient(Environment env) {
        this.env = env;
    }

    @Override
    protected void initImportanceParam() {

    }

    @Override
    public void askAssignWorker() throws SnowflakeWorkerException {
        InetAddress address = NetUtils.getPrivateIp4();
        Assert.state(address != null, "no ipv4");
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

    }


}
