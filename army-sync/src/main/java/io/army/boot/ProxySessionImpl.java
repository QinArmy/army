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
    public List<Integer> update(Update update) {
        return this.sessionContext.currentSession().update(update);
    }

    @Override
    public List<Integer> update(Update update, Visible visible) {
        return this.sessionContext.currentSession().update(update, visible);
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
    public void delete(Delete delete) {
        this.sessionContext.currentSession().delete(delete);
    }

    @Override
    public void delete(Delete delete, Visible visible) {
        this.sessionContext.currentSession().delete(delete, visible);
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
