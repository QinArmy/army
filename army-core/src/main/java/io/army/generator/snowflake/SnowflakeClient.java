package io.army.generator.snowflake;


import java.util.function.Consumer;

/**
*/
public interface SnowflakeClient {


    /**
     * provide to {@link SnowflakeGenerator} to register itself.
     *
     * @return the {@link Worker} that assigned by snowflake server
     */
    boolean registerGenerator(SnowflakeGenerator generator, Consumer<Worker> workerConsumer)
            throws SnowflakeWorkerException;

    Worker currentWorker();

}
