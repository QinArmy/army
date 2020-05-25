package io.army.boot;

import io.army.ProxySession;
import io.army.SessionFactory;
import io.army.SessionOptions;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;

class ProxySessionImpl implements ProxySession {

    private final SessionFactory sessionFactory;

    private final CurrentSessionContext sessionContext;

    ProxySessionImpl(SessionFactory sessionFactory, CurrentSessionContext sessionContext) {
        this.sessionFactory = sessionFactory;
        this.sessionContext = sessionContext;
    }


    @Override
    public boolean hasCurrentSession() {
        return this.sessionContext.hasCurrentSession();
    }

    @Override
    public <T extends IDomain> void save(T domain) {
        this.sessionContext.currentSession().save(domain);
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, Object id) {
        return this.sessionContext.currentSession().get(tableMeta, id);
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible) {
        return this.sessionContext.currentSession().get(tableMeta, id, visible);
    }

    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return this.sessionContext.currentSession().getByUnique(tableMeta, propNameList, valueList);
    }

    @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return this.sessionContext.currentSession().getByUnique(tableMeta, propNameList, valueList, visible);
    }

    @Override
    public <T> T selectOne(Select select, Class<T> resultClass) {
        return this.sessionContext.currentSession().selectOne(select, resultClass);
    }

    @Override
    public <T> T selectOne(Select select, Class<T> resultClass, Visible visible) {
        return this.sessionContext.currentSession().selectOne(select, resultClass, visible);
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass) {
        return this.sessionContext.currentSession().select(select, resultClass);
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass, Visible visible) {
        return this.sessionContext.currentSession().select(select, resultClass, visible);
    }

    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }


    @Override
    public void insert(Insert insert) {
        this.sessionContext.currentSession().insert(insert);
    }

    @Override
    public void insert(Insert insert, Visible visible) {
        this.sessionContext.currentSession().insert(insert, visible);
    }

    @Override
    public int subQueryInsert(Insert insert) {
        return this.sessionContext.currentSession().subQueryInsert(insert);
    }

    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        return this.sessionContext.currentSession().subQueryInsert(insert, visible);
    }

    @Override
    public long subQueryLargeInsert(Insert insert) {
        return this.sessionContext.currentSession().subQueryLargeInsert(insert);
    }

    @Override
    public long subQueryLargeInsert(Insert insert, Visible visible) {
        return this.sessionContext.currentSession().subQueryLargeInsert(insert, visible);
    }

    @Override
    public <T> List<T> returningInsert(Insert insert, Class<T> resultClass) {
        return this.sessionContext.currentSession().returningInsert(insert, resultClass);
    }

    @Override
    public <T> List<T> returningInsert(Insert insert, Class<T> resultClass, Visible visible) {
        return this.sessionContext.currentSession().returningInsert(insert, resultClass, visible);
    }

    @Override
    public int update(Update update) {
        return update(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public int update(Update update, Visible visible) {
        return this.sessionContext.currentSession().update(update, visible);
    }

    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass) {
        return this.sessionContext.currentSession().returningUpdate(update, resultClass);
    }

    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass, Visible visible) {
        return this.sessionContext.currentSession().returningUpdate(update, resultClass, visible);
    }

    @Override
    public int[] batchUpdate(Update update) {
        return batchUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public int[] batchUpdate(Update update, Visible visible) {
        return this.sessionContext.currentSession().batchUpdate(update, visible);
    }

    @Override
    public long largeUpdate(Update update) {
        return largeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        return this.sessionContext.currentSession().largeUpdate(update, visible);
    }

    @Override
    public long[] batchLargeUpdate(Update update) {
        return batchLargeUpdate(update, Visible.ONLY_VISIBLE);
    }

    @Override
    public long[] batchLargeUpdate(Update update, Visible visible) {
        return this.sessionContext.currentSession().batchLargeUpdate(update, visible);
    }

    @Override
    public int delete(Delete delete) {
        return delete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().delete(delete, visible);
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass) {
        return returningDelete(delete, resultClass, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass, Visible visible) {
        return this.sessionContext.currentSession().returningDelete(delete, resultClass, visible);
    }

    @Override
    public int[] batchDelete(Delete delete) {
        return batchDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public int[] batchDelete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().batchDelete(delete, visible);
    }

    @Override
    public long largeDelete(Delete delete) {
        return largeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().largeDelete(delete, visible);
    }

    @Override
    public long[] batchLargeDelete(Delete delete) {
        return batchLargeDelete(delete, Visible.ONLY_VISIBLE);
    }

    @Override
    public long[] batchLargeDelete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().batchLargeDelete(delete, visible);
    }

    @Override
    public SessionOptions options() {
        return this.sessionContext.currentSession().options();
    }

    @Override
    public boolean readonly() {
        return this.sessionContext.currentSession().readonly();
    }

    @Override
    public boolean closed() {
        return this.sessionContext.currentSession().closed();
    }


    @Override
    public boolean hasTransaction() {
        return this.sessionContext.currentSession().hasTransaction();
    }
}
