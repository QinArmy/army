package io.army.sync;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.lang.Nullable;
import io.army.session.CurrentRecord;
import io.army.session._ArmySession;
import io.army.session._ArmySessionFactory;
import io.army.util.ArmyCriteria;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class ArmySyncSession extends _ArmySession implements SyncSession {


    protected ArmySyncSession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {
        super(builder);
    }


    @Nullable
    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.queryOne(statement, resultClass, Visible.ONLY_VISIBLE);
    }


    @Nullable
    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Class<R> resultClass, final Visible visible) {
        final List<R> resultList;
        resultList = this.query(statement, resultClass, _Collections::arrayList, visible);
        return onlyRow(resultList);
    }

    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryOneObject(statement, constructor, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R> R queryOneObject(SimpleDqlStatement statement, Supplier<R> constructor, final Visible visible) {
        return onlyRow(this.queryObject(statement, constructor, _Collections::arrayList, visible));
    }

    @Override
    public final <R> R queryOneRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryOneRecord(statement, function, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> R queryOneRecord(final SimpleDqlStatement statement, final Function<CurrentRecord, R> function,
                                      final Visible visible) {
        return onlyRow(this.queryRecord(statement, function, _Collections::arrayList, visible));
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass) {
        return this.query(statement, resultClass, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.query(statement, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Visible visible) {
        return this.query(statement, resultClass, _Collections::arrayList, visible);
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryObject(statement, constructor, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.queryObject(statement, constructor, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> queryObject(SimpleDqlStatement statement, Supplier<R> constructor, Visible visible) {
        return this.queryObject(statement, constructor, _Collections::arrayList, visible);
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function) {
        return this.queryRecord(statement, function, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                                         Supplier<List<R>> listConstructor) {
        return this.queryRecord(statement, function, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> queryRecord(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                                         Visible visible) {
        return this.queryRecord(statement, function, _Collections::arrayList, visible);
    }


    @Override
    public final <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, StreamOptions options) {
        return this.queryStream(statement, resultClass, options, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> Stream<R> queryObjectStream(SimpleDqlStatement statement, Supplier<R> constructor,
                                                 StreamOptions options) {
        return this.queryObjectStream(statement, constructor, options, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> queryRecardStream(SimpleDqlStatement statement, Function<CurrentRecord, R> function,
                                                 StreamOptions options) {
        return this.queryRecardStream(statement, function, options, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long update(SimpleDmlStatement statement) {
        return this.update(statement, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <T> long save(T domain) {
        return this.update(ArmyCriteria.insertStmt(this, domain), Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T> long save(T domain, Visible visible) {
        return this.update(ArmyCriteria.insertStmt(this, domain), visible);
    }

    @Override
    public final <T> long batchSave(List<T> domainList) {
        return this.update(ArmyCriteria.batchInsertStmt(this, domainList), Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T> long batchSave(List<T> domainList, Visible visible) {
        return this.update(ArmyCriteria.batchInsertStmt(this, domainList), visible);
    }


    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, _Collections::arrayList, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, Visible visible) {
        return this.batchUpdate(statement, _Collections::arrayList, false, visible);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, boolean useMultiStmt) {
        return this.batchUpdate(statement, _Collections::arrayList, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor,
                                        boolean useMultiStmt) {
        return this.batchUpdate(statement, listConstructor, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, boolean useMultiStmt, Visible visible) {
        return this.batchUpdate(statement, _Collections::arrayList, useMultiStmt, visible);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor,
                                        Visible visible) {
        return this.batchUpdate(statement, listConstructor, false, visible);
    }


    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator) {
        return this.batchQuery(statement, resultClass, terminator, _Collections::arrayList, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                        Supplier<List<R>> listConstructor) {
        return this.batchQuery(statement, resultClass, terminator, listConstructor, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                        Supplier<List<R>> listConstructor, boolean useMultiStmt) {
        return this.batchQuery(statement, resultClass, terminator, listConstructor, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                        Visible visible) {
        return this.batchQuery(statement, resultClass, terminator, _Collections::arrayList, false, visible);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                        Supplier<List<R>> listConstructor, Visible visible) {
        return this.batchQuery(statement, resultClass, terminator, listConstructor, false, visible);
    }


    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, R terminator) {
        return this.batchQueryObject(statement, constructor, terminator, _Collections::arrayList, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                              Supplier<List<R>> listConstructor) {
        return this.batchQueryObject(statement, constructor, terminator, listConstructor, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                              Supplier<List<R>> listConstructor, boolean useMultiStmt) {
        return this.batchQueryObject(statement, constructor, terminator, listConstructor, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, R terminator, Visible visible) {
        return this.batchQueryObject(statement, constructor, terminator, _Collections::arrayList, false, visible);
    }

    @Override
    public final <R> List<R> batchQueryObject(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                              Supplier<List<R>> listConstructor, Visible visible) {
        return this.batchQueryObject(statement, constructor, terminator, listConstructor, false, visible);
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                              R terminator) {
        return this.batchQueryRecord(statement, function, terminator, _Collections::arrayList, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                              R terminator, Supplier<List<R>> listConstructor) {
        return this.batchQueryRecord(statement, function, terminator, listConstructor, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                              R terminator, Supplier<List<R>> listConstructor, boolean useMultiStmt) {
        return this.batchQueryRecord(statement, function, terminator, listConstructor, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                              R terminator, Visible visible) {
        return this.batchQueryRecord(statement, function, terminator, _Collections::arrayList, false, visible);
    }

    @Override
    public final <R> List<R> batchQueryRecord(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                              R terminator, Supplier<List<R>> listConstructor, Visible visible) {
        return this.batchQueryRecord(statement, function, terminator, listConstructor, false, visible);
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                                StreamOptions options) {
        return this.batchQueryStream(statement, resultClass, terminator, options, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                                StreamOptions options, boolean useMultiStmt) {
        return this.batchQueryStream(statement, resultClass, terminator, options, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator,
                                                StreamOptions options, Visible visible) {
        return this.batchQueryStream(statement, resultClass, terminator, options, false, visible);
    }

    @Override
    public final <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                                      StreamOptions options) {
        return this.batchQueryObjectStream(statement, constructor, terminator, options, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                                      StreamOptions options, boolean useMultiStmt) {
        return this.batchQueryObjectStream(statement, constructor, terminator, options, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryObjectStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                                      StreamOptions options, Visible visible) {
        return this.batchQueryObjectStream(statement, constructor, terminator, options, false, visible);
    }

    @Override
    public final <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                                      R terminator, StreamOptions options) {
        return this.batchQueryRecordStream(statement, function, terminator, options, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                                      R terminator, StreamOptions options, boolean useMultiStmt) {
        return this.batchQueryRecordStream(statement, function, terminator, options, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryRecordStream(BatchDqlStatement statement, Function<CurrentRecord, R> function,
                                                      R terminator, StreamOptions options, Visible visible) {
        return this.batchQueryRecordStream(statement, function, terminator, options, false, visible);
    }


    @Override
    public final MultiResult multiStmt(MultiResultStatement statement) {
        return this.multiStmt(statement, StreamOptions.LIST_LIKE, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiResult multiStmt(MultiResultStatement statement, StreamOptions options) {
        return this.multiStmt(statement, options, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiResult multiStmt(MultiResultStatement statement, Visible visible) {
        return this.multiStmt(statement, StreamOptions.LIST_LIKE, visible);
    }

    @Override
    public final MultiStream multiStmtStream(MultiResultStatement statement) {
        return this.multiStmtStream(statement, StreamOptions.LIST_LIKE, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiStream multiStmtStream(MultiResultStatement statement, StreamOptions options) {
        return this.multiStmtStream(statement, options, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiStream multiStmtStream(MultiResultStatement statement, Visible visible) {
        return this.multiStmtStream(statement, StreamOptions.LIST_LIKE, visible);
    }


    @Nullable
    private static <R> R onlyRow(final List<R> resultList) {
        final R result;
        switch (resultList.size()) {
            case 1:
                result = resultList.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw _Exceptions.nonUnique(resultList);
        }
        return result;
    }


}
