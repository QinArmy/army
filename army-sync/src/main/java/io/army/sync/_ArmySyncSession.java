package io.army.sync;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.lang.Nullable;
import io.army.session.NonUniqueException;
import io.army.session._ArmySession;
import io.army.util.ArmyCriteria;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class _ArmySyncSession extends _ArmySession implements SyncSession {


    protected _ArmySyncSession(String name, boolean readonly) {
        super(name, readonly);
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
    public final Map<String, Object> queryOneAsMap(SimpleDqlStatement statement) {
        return this.queryOneAsMap(statement, _Collections::hashMap, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Map<String, Object> queryOneAsMap(SimpleDqlStatement statement, Visible visible) {
        return this.queryOneAsMap(statement, _Collections::hashMap, visible);
    }

    @Override
    public final Map<String, Object> queryOneAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor) {
        return this.queryOneAsMap(statement, mapConstructor, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final Map<String, Object> queryOneAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        final List<Map<String, Object>> list;
        list = this.queryAsMap(statement, mapConstructor, _Collections::arrayList, visible);
        final Map<String, Object> result;
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
    public final List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement) {
        return this.queryAsMap(statement, _Collections::hashMap, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Visible visible) {
        return this.queryAsMap(statement, _Collections::hashMap, _Collections::arrayList, visible);
    }

    @Override
    public final List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.queryAsMap(statement, _Collections::hashMap, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }


    @Override
    public final <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, StreamOptions options) {
        return this.queryStream(statement, resultClass, options, Visible.ONLY_VISIBLE);
    }


    @Override
    public final Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement,
                                                            Supplier<Map<String, Object>> mapConstructor,
                                                            StreamOptions options) {
        return this.queryMapStream(statement, mapConstructor, options, Visible.ONLY_VISIBLE);
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
    public final QueryResult batchQuery(BatchDqlStatement statement) {
        return this.batchQuery(statement, Visible.ONLY_VISIBLE);
    }


    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement) {
        return this.batchUpdate(statement, _Collections::arrayList, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, Supplier<List<Long>> listConstructor) {
        return this.batchUpdate(statement, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(BatchDmlStatement statement, Visible visible) {
        return this.batchUpdate(statement, _Collections::arrayList, visible);
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
    public final MultiResult multiStmt(MultiStatement statement) {
        return this.multiStmt(statement, Visible.ONLY_VISIBLE);
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
