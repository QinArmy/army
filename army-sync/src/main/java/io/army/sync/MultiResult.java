package io.army.sync;

import io.army.ArmyException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface MultiResult extends MultiResultSpec {


    <R> List<R> nextQuery(Class<R> resultClass) throws ArmyException;

    <R> List<R> nextQuery(Class<R> resultClass, Supplier<List<R>> listConstructor) throws ArmyException;

    List<Map<String, Object>> nextQueryAsMap() throws ArmyException;

    List<Map<String, Object>> nextQueryAsMap(Supplier<Map<String, Object>> mapConstructor)
            throws ArmyException;

    List<Map<String, Object>> nextQueryAsMap(Supplier<Map<String, Object>> mapConstructor,
                                             Supplier<List<Map<String, Object>>> listConstructor)
            throws ArmyException;





}
