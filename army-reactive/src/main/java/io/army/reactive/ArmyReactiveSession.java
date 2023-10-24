package io.army.reactive;

import io.army.criteria.BatchDmlStatement;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.session.*;
import io.army.util.ArmyCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmyReactiveLocalSession}</li>
 *     <li>{@link ArmyReactiveRmSession}</li>
 * </ul>
 * <p>This class extends {@link _ArmySession} and implementation of {@link ReactiveSession}.
 *
 * @since 1.0
 */
abstract class ArmyReactiveSession extends _ArmySession implements ReactiveSession {

    protected ArmyReactiveSession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {
        super(builder);
    }


    @Override
    public final Mono<?> setSavePoint() {
        return this.setSavePoint(Option.EMPTY_OPTION_FUNC);
    }

    @Override
    public final <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer) {
        return this.query(statement, resultClass, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<R> query(SimpleDqlStatement statement, Class<R> resultClass, ReactiveOption option) {
        return this.query(statement, resultClass, ResultStates.IGNORE_STATES, option);
    }


    @Override
    public final <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.queryOptional(statement, resultClass, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer) {
        return this.queryOptional(statement, resultClass, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<Optional<R>> queryOptional(SimpleDqlStatement statement, Class<R> resultClass, ReactiveOption option) {
        return this.queryOptional(statement, resultClass, ResultStates.IGNORE_STATES, option);
    }


    @Override
    public final <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer) {
        return this.queryObject(statement, constructor, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, ReactiveOption option) {
        return this.queryObject(statement, constructor, ResultStates.IGNORE_STATES, option);
    }


    @Override
    public final <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer) {
        return this.queryRecord(statement, function, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function, ReactiveOption option) {
        return this.queryRecord(statement, function, ResultStates.IGNORE_STATES, option);
    }

    @Override
    public final <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass) {
        return this.batchQuery(statement, resultClass, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, Consumer<ResultStates> consumer) {
        return this.batchQuery(statement, resultClass, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, ReactiveOption option) {
        return this.batchQuery(statement, resultClass, ResultStates.IGNORE_STATES, option);
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor) {
        return this.batchQueryObject(statement, constructor, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, Consumer<ResultStates> consumer) {
        return this.batchQueryObject(statement, constructor, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, ReactiveOption option) {
        return this.batchQueryObject(statement, constructor, ResultStates.IGNORE_STATES, option);
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.batchQueryRecord(statement, function, ResultStates.IGNORE_STATES, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer) {
        return this.batchQueryRecord(statement, function, consumer, defaultOption());
    }

    @Override
    public final <R> Flux<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function, ReactiveOption option) {
        return this.batchQueryRecord(statement, function, ResultStates.IGNORE_STATES, option);
    }


    @Override
    public final Mono<ResultStates> save(Object domain) {
        return this.update(ArmyCriteria.insertStmt(this, domain), defaultOption());
    }

    @Override
    public final Mono<ResultStates> update(SimpleDmlStatement dml) {
        return this.update(dml, defaultOption());
    }

    @Override
    public final <T> Mono<ResultStates> batchSave(List<T> domainList) {
        return this.update(ArmyCriteria.batchInsertStmt(this, domainList), defaultOption());
    }

    @Override
    public final Flux<ResultStates> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, defaultOption());
    }

    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }

    abstract ReactiveOption defaultOption();


}
