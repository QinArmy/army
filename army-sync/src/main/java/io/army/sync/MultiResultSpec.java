package io.army.sync;

import io.army.ArmyException;
import io.army.lang.Nullable;

public interface MultiResultSpec extends AutoCloseable {

    State next() throws ArmyException;

    long updateCount() throws ArmyException;

    @Nullable
    <R> R queryOne(Class<R> resultClass) throws ArmyException;


    @Override
    void close() throws ArmyException;

    enum State {

        NONE,
        UPDATE,
        QUERY
    }


}
