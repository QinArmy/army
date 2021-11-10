package io.army.sync;

import io.army.SessionException;
import io.army.codec.StatementType;
import io.army.criteria.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.session.FactoryMode;
import io.army.tx.TransactionTimeOutException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

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
//        assertSessionActive(false);
//        try {
//            List<R> resultList;
//            // execute sql and extract result
//            resultList = this.genericSessionFactory.selectSQLExecutor()
//                    .select(this, this.dialect.select(select, visible), resultClass);
//            return resultList;
//        } finally {
//            ((InnerSQL) select).clear();
//        }
        return Collections.emptyList();
    }

    @Override
    public final int subQueryInsert(Insert insert, final Visible visible) {
//        assertSessionActive(true);
//        //1. parse update sql
//        final Stmt stmt = parseSubQueryInsert(insert, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.insertSQLExecutor()
//                    .subQueryInsert(this, stmt);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) insert).clear();
//        }
        return 0;
    }

    @Override
    public final long largeSubQueryInsert(Insert insert, final Visible visible) {
//        assertSessionActive(true);
//        //1. parse update sql
//        final Stmt stmt = parseSubQueryInsert(insert, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.insertSQLExecutor()
//                    .subQueryLargeInsert(this, stmt);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) insert).clear();
//        }
        return 0;
    }

    @Override
    public final <R> List<R> returningInsert(Insert insert, Class<R> resultClass, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseReturningInsert(insert, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.insertSQLExecutor()
//                    .returningInsert(this, stmt, resultClass);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) insert).clear();
//        }
        return Collections.emptyList();
    }

    @Override
    public final int update(Update update, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseUpdate(update, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .update(this, stmt, true);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) update).clear();
//        }
        return 0;
    }

    @Override
    public final long largeUpdate(Update update, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseUpdate(update, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .largeUpdate(this, stmt, true);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) update).clear();
//        }
        return 0;
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseUpdate(update, visible);
//        try {   //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .returningUpdate(this, stmt, resultClass, true);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) update).clear();
//        }
        return Collections.emptyList();
    }

    @Override
    public final int delete(Delete delete, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseDelete(delete, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .update(this, stmt, false);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) delete).clear();
//        }
        return 0;
    }

    @Override
    public final long largeDelete(Delete delete, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseDelete(delete, visible);
//        try {
//            //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .largeUpdate(this, stmt, false);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) delete).clear();
//        }
        return 0;
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, final Visible visible) {
//        assertSessionActive(true);
//
//        //1. parse update sql
//        final Stmt stmt = parseDelete(delete, visible);
//        try {   //2. execute sql by connection
//            return this.genericSessionFactory.updateSQLExecutor()
//                    .returningUpdate(this, stmt, resultClass, false);
//        } catch (Throwable e) {
//            markRollbackOnlyForChildUpdate(stmt);
//            throw e;
//        } finally {
//            // 3. clear
//            ((InnerSQL) delete).clear();
//        }
        return Collections.emptyList();
    }

    @Override
    public void close() throws SessionException {
//        if (this.genericClosed) {
//            return;
//        }
//        GenericTransaction tx = obtainTransaction();
//        if (tx != null && !tx.transactionEnded()) {
//            throw new TransactionNotCloseException("Session transaction not close,tx status[%s]"
//                    , tx.status());
//        }
//        try {
//            this.connection.close();
//            this.genericClosed = true;
//        } catch (SQLException e) {
//            throw new SessionCloseFailureException(e, "Connection close failure.");
//        }

    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final int timeToLiveInSeconds() throws TransactionTimeOutException {
//        GenericTransaction tx = obtainTransaction();
//        int liveInsSeconds;
//        if (tx == null) {
//            liveInsSeconds = -1;
//        } else {
//            liveInsSeconds = tx.timeToLiveInSeconds();
//        }
//        return liveInsSeconds;
        return 0;
    }

    @Override
    public final boolean supportSharding() {
        return this.sessionFactory().shardingMode() != FactoryMode.NO_SHARDING;
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






}
