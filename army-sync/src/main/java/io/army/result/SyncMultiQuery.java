package io.army.result;

import io.army.ArmyException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface SyncMultiQuery extends MultiResult, AutoCloseable {

    <R> Stream<R> query(Class<R> resultClass) throws ArmyException;

    <R> Stream<R> query(Class<R> resultClass, Consumer<ResultStates> consumer) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor) throws ArmyException;

    <R> Stream<R> queryObject(Supplier<R> constructor, Consumer<ResultStates> consumer) throws ArmyException;

    <R> Stream<R> queryRecord(Function<? super CurrentRecord, R> function) throws ArmyException;

    <R> Stream<R> queryRecord(Function<? super CurrentRecord, R> function, Consumer<ResultStates> consumer) throws ArmyException;


    @Override
    void close() throws ArmyException;

}
