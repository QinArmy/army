package io.army.reactive.executor;

import io.army.reactive.ReactiveMultiResultSpec;
import io.army.session.CurrentRecord;
import io.army.session.ExecutorSupport;
import io.army.session.ResultStates;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ReactiveExecutorSupport extends ExecutorSupport {

    protected ReactiveExecutorSupport() {
    }


    protected static abstract class ArmyReactiveMultiResultSpec implements ReactiveMultiResultSpec {

        @Override
        public final <R> Flux<R> nextQuery(Class<R> resultClass) {
            return this.nextQuery(resultClass, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Flux<Optional<R>> nextQueryOptional(Class<R> resultClass) {
            return this.nextQueryOptional(resultClass, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Flux<R> nextQueryObject(Supplier<R> constructor) {
            return this.nextQueryObject(constructor, ResultStates.IGNORE_STATES);
        }

        @Override
        public final <R> Flux<R> nextQueryRecord(Function<CurrentRecord, R> function) {
            return this.nextQueryRecord(function, ResultStates.IGNORE_STATES);
        }


    }// ArmyQueryResultSpec


}
