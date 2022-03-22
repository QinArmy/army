package io.army.generator.snowflake;

import java.util.function.Consumer;

public final class SingleJvmSnowflakeClient implements SnowflakeClient {

    public static final SingleJvmSnowflakeClient INSTANCE = new SingleJvmSnowflakeClient();

    private SingleJvmSnowflakeClient() {
    }

    @Override
    public boolean registerGenerator(SnowflakeGenerator generator, Consumer<Worker> workerConsumer)
            throws SnowflakeWorkerException {
        return true;
    }

    @Override
    public Worker currentWorker() {
        return Worker.ZERO;
    }


}
