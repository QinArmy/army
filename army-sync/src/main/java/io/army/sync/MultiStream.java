package io.army.sync;

import io.army.ArmyException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MultiStream extends MultiResultSpec {


    <R> Stream<R> nextQueryStream(Class<R> resultClass) throws ArmyException;

    <R> Stream<R> nextQueryStream(Class<R> resultClass, Supplier<List<R>> listConstructor) throws ArmyException;

    Stream<Map<String, Object>> nextQueryMapStream() throws ArmyException;

    Stream<Map<String, Object>> nextQueryMapStream(Supplier<Map<String, Object>> mapConstructor)
            throws ArmyException;

    Stream<Map<String, Object>> nextQueryMapStream(Supplier<Map<String, Object>> mapConstructor,
                                                   Supplier<List<Map<String, Object>>> listConstructor)
            throws ArmyException;


}
