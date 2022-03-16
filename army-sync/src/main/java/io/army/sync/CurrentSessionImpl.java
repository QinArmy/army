package io.army.sync;

import io.army.SessionException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

class CurrentSessionImpl extends AbstractSyncSession implements CurrentSession {

    private final SessionFactory sessionFactory;


    CurrentSessionImpl(SessionFactory sessionFactory, CurrentSessionContext sessionContext) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public ArmyEnvironment environment() {
        return this.sessionFactory.environment();
    }

    @Override
    public boolean isReadonlySession() {
        return this.currentSession().isReadonlySession();
    }

    @Override
    public boolean closed() {
        return this.currentSession().closed();
    }

    @Override
    public boolean hasTransaction() {
        return this.currentSession().hasTransaction();
    }

    @Override
    public void flush() throws SessionException {
        this.currentSession().flush();
    }

    @Nullable
    @Override
    public <T extends IDomain> TableMeta<T> table(Class<T> domainClass) {
        return this.sessionFactory.tableMeta(domainClass);
    }


    @Override
    public <R extends IDomain> R get(TableMeta<R> table, Object id, Visible visible) {
        return this.currentSession().get(table, id, visible);
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> table, UniqueFieldMeta<R> field, Object value
            , Visible visible) {
        return this.currentSession().getByUnique(table, field, value, visible);
    }

    @Override
    public Map<String, Object> selectOneAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Visible visible) {
        return this.currentSession().selectOneAsMap(select, mapConstructor, visible);
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible) {
        return this.currentSession().select(select, resultClass, listConstructor, visible);
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select, Supplier<Map<String, Object>> mapConstructor
            , Supplier<List<Map<String, Object>>> listConstructor, Visible visible) {
        return this.currentSession().selectAsMap(select, mapConstructor, listConstructor, visible);
    }

    @Override
    public long insert(Insert insert, Visible visible) {
        return this.currentSession().insert(insert, visible);
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Supplier<List<R>> listConstructor
            , Visible visible) {
        return this.currentSession().returningInsert(insert, resultClass, listConstructor, visible);
    }

    @Override
    public long update(Update update, Visible visible) {
        return this.currentSession().update(update, visible);
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass, Supplier<List<R>> listConstructor
            , Visible visible) {
        return this.currentSession().returningUpdate(update, resultClass, listConstructor, visible);
    }

    @Override
    public long delete(Delete delete, Visible visible) {
        return this.currentSession().delete(delete, visible);
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible) {
        return this.currentSession().returningDelete(delete, resultClass, listConstructor, visible);
    }

    @Override
    public List<Long> batchUpdate(Update update, Visible visible) {
        return this.currentSession().batchUpdate(update, visible);
    }

    @Override
    public List<Long> batchDelete(Delete delete, Visible visible) {
        return this.currentSession().batchDelete(delete, visible);
    }

    private Session currentSession() {
        throw new UnsupportedOperationException();
    }

}
