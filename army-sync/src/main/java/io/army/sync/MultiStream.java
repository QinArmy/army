package io.army.sync;

import io.army.ArmyException;
import io.army.session.CurrentRecord;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MultiStream extends MultiResultSpec {


    <R> Stream<R> query(Class<R> resultClass) throws ArmyException;


    <R> Stream<R> query(Class<R> resultClass, StreamOptions options) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor, StreamOptions options) throws ArmyException;

    <R> Stream<R> queryRecord(Function<CurrentRecord, R> function) throws ArmyException;

    <R> Stream<R> queryRecord(Function<CurrentRecord, R> function, StreamOptions options) throws ArmyException;


}
