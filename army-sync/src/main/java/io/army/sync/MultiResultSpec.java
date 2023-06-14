package io.army.sync;

import io.army.ArmyException;
import io.army.lang.Nullable;

public interface MultiResultSpec extends AutoCloseable {

    State hasMore() throws ArmyException;

    long nextUpdate() throws ArmyException;

    @Nullable
    <R> R nextOne(Class<R> resultClass) throws ArmyException;


    @Override
    void close() throws ArmyException;

    enum State {

        NONE,
        UPDATE,
        QUERY
    }


}
