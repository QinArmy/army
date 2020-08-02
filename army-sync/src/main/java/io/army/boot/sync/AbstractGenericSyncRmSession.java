package io.army.boot.sync;

import io.army.DomainUpdateException;
import io.army.ReadOnlySessionException;
import io.army.codec.StatementType;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.tx.GenericTransaction;
import io.army.tx.Isolation;
import io.army.wrapper.ChildBatchSQLWrapper;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;

import java.util.List;

public abstract class AbstractGenericSyncRmSession extends AbstractGenericSyncSession
        implements InnerGenericRmSession {

    private final InnerGenericRmSessionFactory genericSessionFactory;

    final Dialect dialect;

    private final InnerCodecContext codecContext = new CodecContextImpl();

    AbstractGenericSyncRmSession(InnerGenericRmSessionFactory sessionFactory) {
        this.genericSessionFactory = sessionFactory;
        this.dialect = this.genericSessionFactory.dialect();
    }

    @Override
    public final <R> List<R> select(Select select, Class<R> resultClass, final Visible visible) {
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
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseValueInsert(insert, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.insertSQLExecutor()
                    .subQueryInsert(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public final long largeSubQueryInsert(Insert insert, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseValueInsert(insert, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.insertSQLExecutor()
                    .subQueryLargeInsert(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public final int update(Update update, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper, true);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public final long largeUpdate(Update update, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper, true);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public final <R> List<R> returningUpdate(Update update, Class<R> resultClass, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {   //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass, true);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public final int delete(Delete delete, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper, false);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final long largeDelete(Delete delete, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper, false);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final <R> List<R> returningDelete(Delete delete, Class<R> resultClass, final Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {   //2. execute sql by connection
            return this.genericSessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass, false);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final InnerCodecContext codecContext() {
        return this.codecContext;
    }

    @Override
    public final void codecContextStatementType(@Nullable StatementType statementType) {
        this.codecContext.statementType(statementType);
    }

    /*################################## blow package method ##################################*/

    @Nullable
    abstract GenericTransaction obtainTransaction();


    final SQLWrapper parseValueInsert(Insert insert, final Visible visible) {
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


    /*################################## blow private method ##################################*/

    private void assertChildDomain() {
        GenericTransaction tx = obtainTransaction();
        if (tx == null || tx.isolation().level < Isolation.READ_COMMITTED.level) {
            throw new DomainUpdateException("Child domain update must in READ_COMMITTED(+) transaction.");
        }
    }


}
