package io.army.reactive;

import io.army.session.CurrentRecord;
import io.army.session.ResultItem;
import io.army.session.ResultStates;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ReactiveMultiResultSpec {

    <R> Flux<R> nextQuery(Class<R> resultClass);

    <R> Flux<R> nextQuery(Class<R> resultClass, Consumer<ResultStates> consumer);


    <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass);

    /**
     * @param <R> representing select result Java Type.
     */
    <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass, Consumer<ResultStates> consumer);


    <R> Flux<R> nextQueryObject(Supplier<R> constructor);

    <R> Flux<R> nextQueryObject(Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function);

    <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);


    Flux<ResultItem> nextQueryAsFlux();


}
