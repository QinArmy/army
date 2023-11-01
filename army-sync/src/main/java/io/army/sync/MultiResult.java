package io.army.sync;

import io.army.ArmyException;
import io.army.session.record.CurrentRecord;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MultiResult extends MultiResultSpec {


    <R> List<R> query(Class<R> resultClass) throws ArmyException;

    <R> List<R> query(Class<R> resultClass, Supplier<List<R>> listConstructor) throws ArmyException;

    <R> List<R> queryObject(Supplier<R> constructor) throws ArmyException;

    <R> List<R> queryObject(Supplier<R> constructor, Supplier<List<R>> listConstructor) throws ArmyException;

    <R> List<R> queryRecord(Function<CurrentRecord, R> function) throws ArmyException;

    <R> List<R> queryRecord(Function<CurrentRecord, R> function, Supplier<List<R>> listConstructor) throws ArmyException;

}
