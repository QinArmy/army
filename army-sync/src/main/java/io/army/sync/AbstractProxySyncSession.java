package io.army.sync;

import io.army.SessionException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.session.GenericCurrentSession;

import java.util.List;

abstract class AbstractProxySyncSession implements GenericSyncApiSession, GenericCurrentSession, GenericSyncProxySession {


    final CurrentSessionContext sessionContext;

    AbstractProxySyncSession(CurrentSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public boolean isReadonlySession() {
        return this.sessionContext.session().isReadonlySession();
    }

    @Override
    public boolean closed() {
        return this.sessionContext.session().closed();
    }

    @Override
    public boolean hasTransaction() {
        return this.sessionContext.session().hasTransaction();
    }

    @Override
    public boolean hasCurrentSession() {
        return this.sessionContext.hasCurrentSession();
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> tableMeta, Object id) {
        return this.sessionContext.session().get(tableMeta, id);
    }

    @Override
    public <T extends IDomain> T get(TableMeta<T> table, Object id, Visible visible) {
        return this.sessionContext.session().get(table, id, visible);
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
        return this.sessionContext.session().selectOne(select, resultClass);
    }

    @Override
    public <T> T selectOne(Select select, Class<T> resultClass, Visible visible) {
        return this.sessionContext.session().selectOne(select, resultClass, visible);
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass) {
        return this.sessionContext.session().select(select, resultClass);
    }

    @Override
    public <T> List<T> select(Select select, Class<T> resultClass, Visible visible) {
        return this.sessionContext.session().select(select, resultClass, visible);
    }


    @Override
    public long insert(Insert insert) {
        this.sessionContext.session().insert(insert);
        return 0;
    }

    @Override
    public long insert(Insert insert, Visible visible) {
        this.sessionContext.session().insert(insert, visible);
        return 0;
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
        return this.sessionContext.session().returningInsert(insert, resultClass);
    }

    @Override
    public <T> List<T> returningInsert(Insert insert, Class<T> resultClass, Visible visible) {
        return this.sessionContext.session().returningInsert(insert, resultClass, visible);
    }


    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass) {
        return this.sessionContext.session().returningUpdate(update, resultClass);
    }

    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass, Visible visible) {
        return this.sessionContext.session().returningUpdate(update, resultClass, visible);
    }

    @Override
    public long update(Update update) {
        return this.sessionContext.session().update(update);
    }

    @Override
    public long update(Update update, Visible visible) {
        return this.sessionContext.session().update(update, visible);
    }


    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass) {
        return this.sessionContext.session().returningDelete(delete, resultClass);
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass, Visible visible) {
        return this.sessionContext.session().returningDelete(delete, resultClass, visible);
    }

    @Override
    public long delete(Delete delete) {
        return this.sessionContext.session().delete(delete);
    }

    @Override
    public long delete(Delete delete, Visible visible) {
        return this.sessionContext.session().delete(delete, visible);
    }

    @Override
    public void flush() throws SessionException {
        this.sessionContext.session().flush();
    }

}
