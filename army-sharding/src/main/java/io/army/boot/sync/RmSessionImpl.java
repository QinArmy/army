package io.army.boot.sync;

import io.army.SessionCloseFailureException;
import io.army.SessionException;
import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.tx.ArmyXid;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.TransactionNotCloseException;
import io.army.tx.XaTransactionOption;
import io.army.util.CriteriaUtils;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a implementation of {@linkplain RmSession}.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class RmSessionImpl implements InnerRmSession {

    private final InnerRmSessionFactory sessionFactory;

    private final XAConnection xaConnection;

    private final XATransaction transaction;

    private boolean closed;

    RmSessionImpl(InnerRmSessionFactory sessionFactory, XAConnection xaConnection, XaTransactionOption txOption)
            throws SessionException {
        // super(sessionFactory, TmSessionFactoryUtils.getConnection(xaConnection));
        this.sessionFactory = sessionFactory;
        this.xaConnection = xaConnection;

        final Xid xid = new ArmyXid(txOption.globalTransactionId()
                , sessionFactory.actualDatabase()
                , sessionFactory.databaseIndex());

        this.transaction = new XaResourceTransaction(this, xid, txOption);
    }


    @Override
    public final RmSessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public final XATransaction sessionTransaction() throws NoSessionTransactionException {
        if (this.transaction.transactionEnded()) {
            throw new NoSessionTransactionException("XA transaction[%s] ended.", this.transaction.name());
        }
        return this.transaction;
    }

    @Override
    public final boolean readonly() {
        return this.transaction.readOnly();
    }

    @Override
    public final boolean closed() {
        return this.closed;
    }

    @Override
    public final boolean hasTransaction() {
        return !this.transaction.transactionEnded();
    }

    @Override
    public final void flush() throws SessionException {
        //no-op
    }

    @Override
    public final void close() throws SessionException {
        if (this.closed) {
            return;
        }
        // first super close
        //   super.close();

        if (!this.transaction.transactionEnded()) {
            throw new TransactionNotCloseException("Session transaction not close,tx status[%s]"
                    , this.transaction.status());
        }
        try {
            this.xaConnection.close();
            this.closed = true;
        } catch (SQLException e) {
            throw new SessionCloseFailureException(e, "Connection close failure.");
        }

    }

    @Override
    public final <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, final Visible visible) {
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainById(tableMeta, id);
        // 2. execute sql
        return this.selectOne(select, tableMeta.javaType(), visible);
    }



    @Override
    public final void valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible) {
//        assertSessionActive(true);
//
//        List<Stmt> stmtList = parseValueInsert(insert, domainIndexSet, visible);
//        try {
//            this.sessionFactory.insertSQLExecutor()
//                    .valueInsert(this, stmtList);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildInsert(stmtList);
//            throw e;
//        } finally {
//            ((InnerSQL) insert).clear();
//        }
    }

    @Override
    public <R extends IDomain> R get(TableMeta<R> tableMeta, Object id) {
        return null;
    }

    @Override
    public <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList, List<Object> valueList) {
        return null;
    }

    @Override
    public <R> R selectOne(Select select, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> R selectOne(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public Map<String, Object> selectOneAsMap(Select select) {
        return null;
    }

    @Override
    public Map<String, Object> selectOneAsMap(Select select, Visible visible) {
        return null;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> select(Select select, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectAsMap(Select select, Visible visible) {
        return null;
    }

    @Override
    public void valueInsert(Insert insert) {

    }

    @Override
    public void valueInsert(Insert insert, Visible visible) {

    }

    @Override
    public int subQueryInsert(Insert insert) {
        return 0;
    }

    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        return 0;
    }

    @Override
    public long subQueryLargeInsert(Insert insert) {
        return 0;
    }

    @Override
    public long largeSubQueryInsert(Insert insert, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> returningInsert(Insert insert, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int update(Update update) {
        return 0;
    }

    @Override
    public int update(Update update, Visible visible) {
        return 0;
    }

    @Override
    public long largeUpdate(Update update) {
        return 0;
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> returningUpdate(Update update, Class<R> resultClass, Visible visible) {
        return null;
    }

    @Override
    public int delete(Delete delete) {
        return 0;
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public long largeDelete(Delete delete) {
        return 0;
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        return 0;
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass) {
        return null;
    }

    @Override
    public <R> List<R> returningDelete(Delete delete, Class<R> resultClass, Visible visible) {
        return null;
    }


//    @Override
//    public InnerCodecContext codecContext() {
//        return null;
//    }


    /*################################## blow package method ##################################*/



    /*################################## blow InnerGenericRmSession method ##################################*/

    @Override
    public final XAResource xaResource() throws SQLException {
        return this.xaConnection.getXAResource();
    }

}
