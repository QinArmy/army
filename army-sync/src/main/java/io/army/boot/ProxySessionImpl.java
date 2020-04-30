package io.army.boot;

import io.army.ProxySession;
import io.army.SessionOptions;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Pair;
import io.army.util.Triple;

import java.util.List;

class ProxySessionImpl implements ProxySession {

    private final CurrentSessionContext sessionContext;

    ProxySessionImpl(CurrentSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }


    @Override
    public boolean hasCurrentSession() {
        return this.sessionContext.hasCurrentSession();
    }

    @Override
    public void save(IDomain entity) {
        this.sessionContext.currentSession().save(entity);
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
    public <F, S> List<Pair<F, S>> selectPair(Select select, Class<F> firstClass, Class<S> secondClass) {
        return this.sessionContext.currentSession().selectPair(select, firstClass, secondClass);
    }

    @Override
    public <F, S> List<Pair<F, S>> selectPair(Select select, Class<F> firstClass, Class<S> secondClass, Visible visible) {
        return this.sessionContext.currentSession().selectPair(select, firstClass, secondClass, visible);
    }

    @Override
    public <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Class<F> firstClass, Class<S> secondClass
            , Class<T> thirdClass) {
        return this.sessionContext.currentSession().selectTriple(select, firstClass, secondClass, thirdClass);
    }

    @Override
    public <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Class<F> firstClass, Class<S> secondClass
            , Class<T> thirdClass, Visible visible) {
        return this.sessionContext.currentSession().selectTriple(select, firstClass, secondClass, thirdClass, visible);
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
