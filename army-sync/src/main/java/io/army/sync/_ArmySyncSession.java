package io.army.sync;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.lang.Nullable;
import io.army.session.NonUniqueException;
import io.army.session._ArmySession;
import io.army.session._ArmySessionFactory;
import io.army.util.ArmyCriteria;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class _ArmySyncSession extends _ArmySession implements SyncSession {


    protected _ArmySyncSession(_ArmySessionFactory.ArmySessionBuilder<?, ?> builder) {
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
        final List<R> list;
        list = this.query(statement, resultClass, _Collections::arrayList, visible);
        final R result;
        switch (list.size()) {
            case 1:
                result = list.get(0);
                break;
            case 0:
                result = null;
                break;
            default: {
                String m = String.format("select result[%s] more than 1.", list.size());
                throw new NonUniqueException(m);
            }
        }
        return result;
    }

    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.queryOne(statement, constructor, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R> R queryOne(SimpleDqlStatement statement, Supplier<R> constructor, final Visible visible) {
        final List<R> list;
        list = this.query(statement, constructor, _Collections::arrayList, visible);
        final R result;
        switch (list.size()) {
            case 1:
                result = list.get(0);
                break;
            case 0:
                result = null;
                break;
            default:
                throw _Exceptions.nonUnique(list);
        }
        return result;
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
    public final <R> List<R> query(SimpleDqlStatement statement, Supplier<R> constructor) {
        return this.query(statement, constructor, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> query(SimpleDqlStatement statement, Supplier<R> constructor, Supplier<List<R>> listConstructor) {
        return this.query(statement, constructor, listConstructor, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, StreamOptions options) {
        return this.queryStream(statement, resultClass, options, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> Stream<R> queryStream(SimpleDqlStatement statement, Supplier<R> constructor, StreamOptions options) {
        return this.queryStream(statement, constructor, options, Visible.ONLY_VISIBLE);
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
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator) {
        return this.batchQuery(statement, constructor, terminator, _Collections::arrayList, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                        Supplier<List<R>> listConstructor) {
        return this.batchQuery(statement, constructor, terminator, listConstructor, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                        Supplier<List<R>> listConstructor, boolean useMultiStmt) {
        return this.batchQuery(statement, constructor, terminator, listConstructor, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator, Visible visible) {
        return this.batchQuery(statement, constructor, terminator, _Collections::arrayList, false, visible);
    }

    @Override
    public final <R> List<R> batchQuery(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                        Supplier<List<R>> listConstructor, Visible visible) {
        return this.batchQuery(statement, constructor, terminator, listConstructor, false, visible);
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
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                                StreamOptions options) {
        return this.batchQueryStream(statement, constructor, terminator, options, false, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                                StreamOptions options, boolean useMultiStmt) {
        return this.batchQueryStream(statement, constructor, terminator, options, useMultiStmt, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Supplier<R> constructor, R terminator,
                                                StreamOptions options, Visible visible) {
        return this.batchQueryStream(statement, constructor, terminator, options, false, visible);
    }


    @Override
    public final MultiResult multiStmt(MultiResultStatement statement) {
        return this.multiStmt(statement, null, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiResult multiStmt(MultiResultStatement statement, @Nullable StreamOptions options) {
        return this.multiStmt(statement, options, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiResult multiStmt(MultiResultStatement statement, Visible visible) {
        return this.multiStmt(statement, null, visible);
    }

    @Override
    public final MultiStream multiStmtStream(MultiResultStatement statement) {
        return this.multiStmtStream(statement, null, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiStream multiStmtStream(MultiResultStatement statement, @Nullable StreamOptions options) {
        return this.multiStmtStream(statement, options, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiStream multiStmtStream(MultiResultStatement statement, Visible visible) {
        return this.multiStmtStream(statement, null, visible);
    }


    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }


}
