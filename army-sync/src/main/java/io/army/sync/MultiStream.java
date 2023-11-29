package io.army.sync;

import io.army.ArmyException;
import io.army.session.record.CurrentRecord;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MultiStream extends MultiResultSpec {


    <R> Stream<R> query(Class<R> resultClass) throws ArmyException;


    <R> Stream<R> query(Class<R> resultClass, StreamOption options) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor, StreamOption options) throws ArmyException;

    <R> Stream<R> queryRecord(Function<CurrentRecord, R> function) throws ArmyException;

    <R> Stream<R> queryRecord(Function<CurrentRecord, R> function, StreamOption options) throws ArmyException;


}
