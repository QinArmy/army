package io.army.boot.sync;

import io.army.*;
import io.army.codec.StatementType;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.tx.GenericTransaction;
import io.army.tx.Isolation;
import io.army.tx.TransactionNotCloseException;
import io.army.tx.TransactionTimeOutException;
import io.army.wrapper.ChildBatchSQLWrapper;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

abstract class AbstractGenericSyncRmSession extends AbstractGenericSyncSession
        implements InnerGenericRmSession {

    private final InnerGenericRmSessionFactory genericSessionFactory;

    final Connection connection;

    final Dialect dialect;

    private final InnerCodecContext codecContext = new CodecContextImpl();

    private boolean genericClosed;

    AbstractGenericSyncRmSession(InnerGenericRmSessionFactory sessionFactory, Connection connection) {
        this.genericSessionFactory = sessionFactory;
        this.connection = connection;
        this.dialect = this.genericSessionFactory.dialect();
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, final Visible visible) {
        assertSessionActive(false);
        try {
            List<R> resultList;
            // execute sql and extract result
            resultList = this.genericSessionFactory.selectSQLExecutor()
                    .select(this, this.dialect.select(select, visible), resultClass);
            return resultList;
        } finally {
            ((InnerSQL) select).clear();
        }
    }

    @Override
    public final int subQueryInsert(Insert insert, final Visible visible) {
        assertSessionActive(true);
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseSubQueryInsert(insert, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.insertSQLExecutor()
                    .subQueryInsert(this, sqlWrapper);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public final long largeSubQueryInsert(Insert insert, final Visible visible) {
        assertSessionActive(true);
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseSubQueryInsert(insert, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.insertSQLExecutor()
                    .subQueryLargeInsert(this, sqlWrapper);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseReturningInsert(insert, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.insertSQLExecutor()
                    .returningInsert(this, sqlWrapper, resultClass);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public final int update(Update update, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper, true);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public final long largeUpdate(Update update, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper, true);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {   //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass, true);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public final int delete(Delete delete, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper, false);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final long largeDelete(Delete delete, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper, false);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, final Visible visible) {
        assertSessionActive(true);

        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {   //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass, false);
        } catch (Throwable e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public void close() throws SessionException {
        if (this.genericClosed) {
            return;
        }
        GenericTransaction tx = obtainTransaction();
        if (tx != null && !tx.transactionEnded()) {
            throw new TransactionNotCloseException("Session transaction not close,tx status[%s]"
                    , tx.status());
        }
        try {
            this.connection.close();
            this.genericClosed = true;
        } catch (SQLException e) {
            throw new SessionCloseFailureException(e, "Connection close failure.");
        }
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final int timeToLiveInSeconds() throws TransactionTimeOutException {
        GenericTransaction tx = obtainTransaction();
        int liveInsSeconds;
        if (tx == null) {
            liveInsSeconds = -1;
        } else {
            liveInsSeconds = tx.timeToLiveInSeconds();
        }
        return liveInsSeconds;
    }

    @Override
    public final boolean supportSharding() {
        return this.sessionFactory().shardingMode() != ShardingMode.NO_SHARDING;
    }

    @Override
    public final InnerCodecContext codecContext() {
        return this.codecContext;
    }

    @Override
    public final void codecContextStatementType(@Nullable StatementType statementType) {
        this.codecContext.statementType(statementType);
    }

    @Override
    public final PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException {
        return this.connection.prepareStatement(sql
                , generatedKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
    }

    @Override
    public final PreparedStatement createStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    @Override
    public final PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }

    @Override
    public final Connection connection() {
        return this.connection;
    }
    /*################################## blow package method ##################################*/

    @Nullable
    abstract GenericTransaction obtainTransaction();


    final SQLWrapper parseSubQueryInsert(Insert insert, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.subQueryInsert(insert, visible);
        if (sqlWrapper instanceof ChildSQLWrapper) {
            assertChildDomain();

        }
        return sqlWrapper;
    }

    final SQLWrapper parseReturningInsert(Insert insert, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        SQLWrapper sqlWrapper = this.dialect.returningInsert(insert, visible);
        if (sqlWrapper instanceof ChildSQLWrapper) {
            assertChildDomain();

        }
        return sqlWrapper;
    }

    final List<SQLWrapper> parseValueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet
            , final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        List<SQLWrapper> sqlWrapperList = this.dialect.valueInsert(insert, domainIndexSet, visible);
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper) {
                assertChildDomain();

            }
        }
        return sqlWrapperList;
    }

    final SQLWrapper parseUpdate(Update update, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.update(update, visible);
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    final SQLWrapper parseDelete(Delete delete, final Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.delete(delete, visible);
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    final void markRollbackOnlyForChildUpdate(SQLWrapper sqlWrapper) {
        GenericTransaction transaction = obtainTransaction();
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            if (transaction != null) {
                transaction.markRollbackOnly();
            }
        }
    }

    final void markRollbackOnlyForChildInsert(List<SQLWrapper> sqlWrapperList) {
        GenericTransaction transaction = obtainTransaction();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
                if (transaction != null) {
                    transaction.markRollbackOnly();
                    break;
                }

            }
        }
    }

    final void assertSessionActive(final boolean write) {
        GenericTransaction tx = obtainTransaction();
        if (this.closed() || (tx != null && tx.nonActive())) {
            String txName = this.sessionTransaction().name();
            throw new SessionUsageException(ErrorCode.SESSION_CLOSED
                    , "TmSession[%s] closed or Transaction[%s] not active.", txName, txName);
        }
        if (write && this.readonly()) {
            throw new ReadOnlySessionException("%s read only");
        }
    }


    /*################################## blow private method ##################################*/

    private void assertChildDomain() {
        GenericTransaction tx = obtainTransaction();
        if (tx == null || tx.isolation().level < Isolation.READ_COMMITTED.level) {
            throw new DomainUpdateException("Child domain update must in READ_COMMITTED(+) transaction.");
        }
    }


}