package io.army.sync;

import io.army.sync.executor.SyncExecutor;

@FunctionalInterface
public interface StreamCommander {


    /**
     * <p>Request the {@link java.util.stream.Stream} to stop sending data and clean up resources.
     * <p>Blocking {@link SyncExecutor} will close underling resources ,
     * for example {@code  java.sql.ResultSet} and {@code  java.sql.Statement}, after this method.
     *
     * <p>If {@link java.util.stream.Stream} or underling resources have closed , army will ignore this method
     */
    void cancel();

}
