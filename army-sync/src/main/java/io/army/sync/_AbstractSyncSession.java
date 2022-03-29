package io.army.sync;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;
import io.army.session.NonUniqueException;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class _AbstractSyncSession implements SyncSession {

    protected static final String SQL_LOG_FORMAT = "army will execute sql:\n{}";

    protected _AbstractSyncSession() {
    }

    @Nullable
    @Override
    public final <R extends IDomain> R get(TableMeta<R> table, Object id) {
        return this.get(table, id, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value) {
        return this.getByUnique(table, field, value, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass) {
        return this.selectOne(select, resultClass, Visible.ONLY_VISIBLE);
    }


    @Nullable
    @Override
    public final <R> R selectOne(Select select, Class<R> resultClass, final Visible visible) {
        final List<R> list;
        list = this.select(select, resultClass, ArrayList::new, visible);
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
    public final Map<String, Object> selectOneAsMap(Select select) {
        return this.selectOneAsMap(select, HashMap::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final Map<String, Object> selectOneAsMap(Select select, Visible visible) {
        return this.selectOneAsMap(select, HashMap::new, visible);
    }

    @Override
    public final Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor) {
        return this.selectOneAsMap(select, mapConstructor, Visible.ONLY_VISIBLE);
    }

    @Nullable
    @Override
    public final Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        final List<Map<String, Object>> list;
        list = this.selectAsMap(select, mapConstructor, ArrayList::new, visible);
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
    public final <R> List<R> select(Select select, Class<R> resultClass) {
        return this.select(select, resultClass, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.select(select, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return this.select(select, resultClass, ArrayList::new, visible);
    }


    @Override
    public final List<Map<String, Object>> selectAsMap(Select select) {
        return this.selectAsMap(select, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> selectAsMap(Select select, Visible visible) {
        return this.selectAsMap(select, HashMap::new, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.selectAsMap(select, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> void save(T domain) {
        this.save(domain, NullHandleMode.INSERT_DEFAULT, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> void save(T domain, Visible visible) {
        this.save(domain, NullHandleMode.INSERT_DEFAULT, visible);
    }

    @Override
    public final <T extends IDomain> void save(T domain, NullHandleMode mode) {
        this.save(domain, mode, Visible.ONLY_VISIBLE);
    }

    @Override
    public final long update(DmlStatement dml) {
        return this.update(dml, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass) {
        return this.returningUpdate(dml, resultClass, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Visible visible) {
        return this.returningUpdate(dml, resultClass, ArrayList::new, visible);
    }

    @Override
    public final <R> List<R> returningUpdate(DmlStatement dml, Class<R> resultClass, Supplier<List<R>> listConstructor) {
        return this.returningUpdate(dml, resultClass, listConstructor, Visible.ONLY_VISIBLE);
    }


    @Override
    public final List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml) {
        return this.returningUpdateAsMap(dml, HashMap::new, ArrayList::new, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Visible visible) {
        return this.returningUpdateAsMap(dml, HashMap::new, ArrayList::new, visible);
    }

    @Override
    public final List<Map<String, Object>> returningUpdateAsMap(DmlStatement dml, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor) {
        return this.returningUpdateAsMap(dml, mapConstructor, listConstructor, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> void batchSave(List<T> domainList) {
        this.batchSave(domainList, NullHandleMode.INSERT_DEFAULT, Visible.ONLY_VISIBLE);
    }

    @Override
    public final <T extends IDomain> void batchSave(List<T> domainList, Visible visible) {
        this.batchSave(domainList, NullHandleMode.INSERT_DEFAULT, visible);
    }

    @Override
    public final <T extends IDomain> void batchSave(List<T> domainList, NullHandleMode mode) {
        this.batchSave(domainList, mode, Visible.ONLY_VISIBLE);
    }

    @Override
    public final List<Long> batchUpdate(NarrowDmlStatement dml) {
        return this.batchUpdate(dml, Visible.ONLY_VISIBLE);
    }

    @Override
    public final MultiResult multiStmt(List<Statement> statementList) {
        return this.multiStmt(statementList, Visible.ONLY_VISIBLE);
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
