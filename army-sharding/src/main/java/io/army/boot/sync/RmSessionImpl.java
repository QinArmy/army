package io.army.boot.sync;

import io.army.SessionException;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.tx.*;
import io.army.util.CriteriaUtils;

import javax.sql.XAConnection;
import javax.transaction.xa.Xid;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is a implementation of {@linkplain RmSession}.
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class RmSessionImpl extends AbstractGenericSyncRmSession implements InnerRmSession {

    private final InnerRmSessionFactory sessionFactory;

    private final XAConnection connection;

    private final XATransaction transaction;

    private boolean closed;

    RmSessionImpl(InnerRmSessionFactory sessionFactory, XAConnection connection, XaTransactionOption txOption)
            throws SessionException {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        this.connection = connection;

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
        return this.transaction != null;
    }

    @Override
    public void flush() throws SessionException {
        //no-op
    }

    @Override
    public void close() throws SessionException {

    }

    @Override
    public final <R extends IDomain> R get(TableMeta<R> tableMeta, Object id, final Visible visible) {
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainById(tableMeta, id);
        // 2. execute sql
        return this.selectOne(select, tableMeta.javaType(), visible);
    }

    @Override
    public final <R extends IDomain> R getByUnique(TableMeta<R> tableMeta, List<String> propNameList
            , List<Object> valueList, final Visible visible) {
        // 1. create sql
        Select select = CriteriaUtils.createSelectDomainByUnique(tableMeta, propNameList, valueList);
        // 2. execute sql
        return this.selectOne(select, tableMeta.javaType(), visible);
    }


    /*################################## blow package method ##################################*/

    @Override
    final GenericTransaction obtainTransaction() {
        return this.transaction;
    }

    /*################################## blow InnerGenericRmSession method ##################################*/

    @Override
    public final XAConnection connection() {
        return this.connection;
    }

    @Override
    public PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement createStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }


}
