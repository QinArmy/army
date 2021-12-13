package io.army.sync;

import io.army.SessionException;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.session.GenericProxySession;

import java.util.List;

abstract class AbstractProxySyncSession implements GenericSyncApiSession, GenericProxySession, GenericSyncProxySession {


    final CurrentSessionContext sessionContext;

    AbstractProxySyncSession(CurrentSessionContext sessionContext) {
        this.sessionContext = sessionContext;
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

    @Override
    public boolean hasCurrentSession() {
        return this.sessionContext.hasCurrentSession();
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, Object id) {
        return this.sessionContext.currentSession().get(tableMeta, id);
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible) {
        return this.sessionContext.currentSession().get(tableMeta, id, visible);
    }

    //@Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return null;
    }

    // @Override
    public <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return null;
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
    public void valueInsert(Insert insert) {
        this.sessionContext.currentSession().valueInsert(insert);
    }

    @Override
    public void valueInsert(Insert insert, Visible visible) {
        this.sessionContext.currentSession().valueInsert(insert, visible);
    }

    //@Override
    public int subQueryInsert(Insert insert) {
        // return this.sessionContext.currentSession().subQueryInsert(insert);
        return 0;
    }

    //@Override
    public int subQueryInsert(Insert insert, Visible visible) {
        // return this.sessionContext.currentSession().subQueryInsert(insert, visible);
        return 0;
    }

    // @Override
    public long subQueryLargeInsert(Insert insert) {
        //return this.sessionContext.currentSession().subQueryLargeInsert(insert);
        return 0;
    }

    //@Override
    public long largeSubQueryInsert(Insert insert, Visible visible) {
        // return this.sessionContext.currentSession().largeSubQueryInsert(insert, visible);
        return 0;
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
        return this.sessionContext.currentSession().update(update);
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
    public long largeUpdate(Update update) {
        return this.sessionContext.currentSession().largeUpdate(update);
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        return this.sessionContext.currentSession().largeUpdate(update, visible);
    }

    @Override
    public int delete(Delete delete) {
        return this.sessionContext.currentSession().delete(delete);
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().delete(delete, visible);
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass) {
        return this.sessionContext.currentSession().returningDelete(delete, resultClass);
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass, Visible visible) {
        return this.sessionContext.currentSession().returningDelete(delete, resultClass, visible);
    }

    @Override
    public long largeDelete(Delete delete) {
        return this.sessionContext.currentSession().largeDelete(delete);
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        return this.sessionContext.currentSession().largeDelete(delete, visible);
    }

    @Override
    public void flush() throws SessionException {
        this.sessionContext.currentSession().flush();
    }

}
