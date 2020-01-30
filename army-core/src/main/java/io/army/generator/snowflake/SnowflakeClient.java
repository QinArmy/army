package io.army.generator.snowflake;


/**
 *
 */
public interface SnowflakeClient {

    /**
     * @throws SnowflakeWorkerException when assign failure
     */
    void askAssignWorker() throws SnowflakeWorkerException;

    void heartbeat() throws SnowflakeWorkerException;

    /**
     * provide to {@link SnowflakeGenerator} to register itself.
     * @return the {@link Worker} that assigned by snowflake server
     */
     boolean registerGenerator(SnowflakeGenerator generator) throws SnowflakeWorkerException;

     Worker currentWorker();


    enum Action {
        ASSGIN, HEARTNEAT, REVOKE

    }
}
