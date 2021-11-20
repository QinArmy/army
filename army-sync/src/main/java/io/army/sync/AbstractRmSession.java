package io.army.sync;

import io.army.*;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.session.GenericRmSessionFactory;
import io.army.stmt.Stmt;
import io.army.sync.executor.StmtExecutor;
import io.army.tx.GenericTransaction;
import io.army.tx.TransactionTimeOutException;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

abstract class AbstractRmSession extends AbstractGenericSyncSession
        implements GenericSyncRmSession {

    final StmtExecutor stmtExecutor;

    final Dialect dialect;
    final Function<ArmyException, RuntimeException> exceptionFunction;

    AbstractRmSession(GenericRmSessionFactory sessionFactory, StmtExecutor stmtExecutor) {
        this.stmtExecutor = stmtExecutor;
        this.dialect = sessionFactory.dialect();
        this.exceptionFunction = sessionFactory.exceptionFunction();
    }


    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, final Visible visible) {
        try {
            assertSessionActive(false);
            final Stmt stmt;
            stmt = this.dialect.select(select, visible);
            return this.stmtExecutor.select(stmt, timeToLiveInSeconds(), resultClass);
        } catch (ArmyException e) {
            throw this.exceptionFunction.apply(e);
        } catch (RuntimeException e) {
            throw this.exceptionFunction.apply(new ArmyUnknownException(e));
        } finally {
            ((_Statement) select).clear();
        }
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




    /*################################## blow package method ##################################*/

    @Nullable
    abstract GenericTransaction obtainTransaction();


    /*################################## blow private method ##################################*/

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

    private int timeToLiveInSeconds() throws TransactionTimeOutException {
        final GenericTransaction tx = obtainTransaction();
        int liveInsSeconds;
        if (tx == null) {
            liveInsSeconds = -1;
        } else {
            liveInsSeconds = tx.timeToLiveInSeconds();
        }
        return liveInsSeconds;
    }

}
