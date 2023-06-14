package io.army.sync;

import io.army.ArmyException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface MultiResult extends MultiResultSpec {


    <R> List<R> query(Class<R> resultClass) throws ArmyException;

    <R> List<R> query(Class<R> resultClass, Supplier<List<R>> listConstructor) throws ArmyException;

    List<Map<String, Object>> queryMap() throws ArmyException;

    List<Map<String, Object>> queryMap(Supplier<Map<String, Object>> mapConstructor)
            throws ArmyException;

    List<Map<String, Object>> queryMap(Supplier<Map<String, Object>> mapConstructor,
                                       Supplier<List<Map<String, Object>>> listConstructor)
            throws ArmyException;


}
