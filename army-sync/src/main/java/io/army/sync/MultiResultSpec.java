package io.army.sync;

import io.army.ArmyException;
import io.army.lang.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public interface MultiResultSpec extends AutoCloseable {

    State next() throws ArmyException;

    long updateCount() throws ArmyException;

    @Nullable
    <R> R queryOne(Class<R> resultClass) throws ArmyException;

    @Nullable
    Map<String, Object> queryOneMap() throws ArmyException;

    @Nullable
    Map<String, Object> queryOneMap(Supplier<Map<String, Object>> mapConstructor) throws ArmyException;

    @Override
    void close() throws ArmyException;

    enum State {

        NONE,
        UPDATE,
        QUERY
    }


}
