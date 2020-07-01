package io.army.boot;

import io.army.SessionException;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.tx.GenericSyncTransaction;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;

import javax.transaction.TransactionalException;
import java.sql.Connection;
import java.util.List;

final class RmSessionImpl extends AbstractSession {

    RmSessionImpl(InnerSyncSessionFactory sessionFactory, Connection connection)
            throws SessionException {
        super(sessionFactory, connection);
    }

    @Override
    Transaction obtainSessionTransaction() {
        return null;
    }

    @Override
    public Transaction sessionTransaction() throws NoSessionTransactionException {
        return null;
    }

    @Override
    public void closeTransaction(GenericSyncTransaction transaction) {

    }

    @Override
    public void close() throws SessionException {

    }

    @Override
    public void flush() throws SessionException {

    }

    @Override
    public TransactionBuilder builder(boolean readOnly, Isolation isolation, int timeoutSeconds)
            throws TransactionalException {
        return null;
    }

    @Override
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id) {
        return null;
    }

    @Override
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, Visible visible) {
        return null;
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return null;
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible) {
        return null;
    }

    @Override
    public boolean readonly() {
        return false;
    }

    @Override
    public boolean closed() {
        return false;
    }

    @Override
    public boolean hasTransaction() {
        return false;
    }
}
