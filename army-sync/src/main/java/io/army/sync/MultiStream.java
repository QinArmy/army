package io.army.sync;

import io.army.ArmyException;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MultiStream extends MultiResultSpec {


    <R> Stream<R> query(Class<R> resultClass) throws ArmyException;


    <R> Stream<R> query(Class<R> resultClass, StreamOptions options) throws ArmyException;

    Stream<Map<String, Object>> queryMap() throws ArmyException;

    Stream<Map<String, Object>> queryMap(Supplier<Map<String, Object>> mapConstructor)
            throws ArmyException;

    Stream<Map<String, Object>> queryMap(StreamOptions options) throws ArmyException;

    Stream<Map<String, Object>> queryMap(Supplier<Map<String, Object>> mapConstructor, StreamOptions options)
            throws ArmyException;


}
