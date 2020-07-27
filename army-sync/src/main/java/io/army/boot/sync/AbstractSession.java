package io.army.boot.sync;

import io.army.*;
import io.army.codec.StatementType;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.sync.SessionFactory;
import io.army.tx.Isolation;
import io.army.tx.Transaction;
import io.army.wrapper.ChildBatchSQLWrapper;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

abstract class AbstractSession extends AbstractGenericSyncApiSession implements InnerSession, InnerTxSession {

    final InnerSyncSessionFactory sessionFactory;

    final Connection connection;

    final boolean readonly;

    final Dialect dialect;

    final InnerCodecContext codecContext = new CodecContextImpl();

    AbstractSession(InnerSyncSessionFactory sessionFactory, Connection connection)
            throws SessionException {
        this.sessionFactory = sessionFactory;
        this.connection = connection;
        this.readonly = sessionFactory.readonly();

        this.dialect = sessionFactory.dialect();

    }



    @Nullable
    @Override
    public <T> T selectOne(Select select, Class<T> resultClass, Visible visible) {
        List<T> list = select(select, resultClass, visible);
        T t;
        if (list.size() == 1) {
            t = list.get(0);
        } else if (list.size() == 0) {
            t = null;
        } else {
            throw new NonUniqueException("select result[%s] more than 1.", list.size());
        }
        return t;
    }


    @Override
    public <T> List<T> select(Select select, Class<T> resultClass, Visible visible) {
        try {
            List<T> resultList;
            // execute sql and extract result
            resultList = this.sessionFactory.selectSQLExecutor()
                    .select(this, this.dialect.select(select, visible), resultClass);

            return resultList;
        } finally {
            ((InnerSQL) select).clear();
        }
    }

    @Override
    public int update(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public <T> List<T> returningUpdate(Update update, Class<T> resultClass, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {   //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public int[] batchUpdate(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public long largeUpdate(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }


    @Override
    public long[] batchLargeUpdate(Update update, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseUpdate(update, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchLargeUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) update).clear();
        }
    }

    @Override
    public void valueInsert(Insert insert, final Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        try {
            //2. execute sql by connection
            this.sessionFactory.insertSQLExecutor()
                    .valueInsert(this, sqlWrapperList);
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }


    @Override
    public int subQueryInsert(Insert insert, Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        if (sqlWrapperList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "insert isn't sub query insert.");
        }
        try {
            //2. execute sql by connection
            return this.sessionFactory.insertSQLExecutor()
                    .subQueryInsert(this, sqlWrapperList.get(0));
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public long largeSubQueryInsert(Insert insert, Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        if (sqlWrapperList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "insert isn't sub query insert.");
        }
        try {
            //2. execute sql by connection
            return this.sessionFactory.insertSQLExecutor()
                    .subQueryLargeInsert(this, sqlWrapperList.get(0));
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }


    @Override
    public <T> List<T> returningInsert(Insert insert, Class<T> resultClass, Visible visible) {
        //1. parse update sql
        final List<SQLWrapper> sqlWrapperList = parseInsert(insert, visible);
        if (sqlWrapperList.size() != 1) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "insert isn't returning");
        }
        try {
            //2. execute sql by connection
            return this.sessionFactory.insertSQLExecutor()
                    .returningInsert(this, sqlWrapperList.get(0), resultClass);
        } catch (Exception e) {
            markRollbackOnlyForChildInsert(sqlWrapperList);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) insert).clear();
        }
    }

    @Override
    public int delete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .update(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public <T> List<T> returningDelete(Delete delete, Class<T> resultClass, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .returningUpdate(this, sqlWrapper, resultClass);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }


    @Override
    public int[] batchDelete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public long largeDelete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .largeUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public long[] batchLargeDelete(Delete delete, Visible visible) {
        //1. parse update sql
        final SQLWrapper sqlWrapper = parseDelete(delete, visible);
        try {
            //2. execute sql by connection
            return this.sessionFactory.updateSQLExecutor()
                    .batchLargeUpdate(this, sqlWrapper);
        } catch (Exception e) {
            markRollbackOnlyForChildUpdate(sqlWrapper);
            throw e;
        } finally {
            // 3. clear
            ((InnerSQL) delete).clear();
        }
    }

    @Override
    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }


    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    /*################################## blow InnerTxSession method ##################################*/

    @Override
    public Connection connection() {
        return this.connection;
    }

    /*################################## blow InnerSession method ##################################*/

    @Override
    public PreparedStatement createStatement(String sql, boolean generatedKey)
            throws SQLException {
        int type;
        if (generatedKey) {
            type = Statement.RETURN_GENERATED_KEYS;
        } else {
            type = Statement.NO_GENERATED_KEYS;
        }
        return connection.prepareStatement(sql, type);
    }

    @Override
    public PreparedStatement createStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    @Override
    public PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }

    @Override
    public InnerCodecContext codecContext() {
        return this.codecContext;
    }

    @Override
    public void codecContextStatementType(@Nullable StatementType statementType) {
        this.codecContext.statementType(statementType);
    }

    /*################################## blow package method ##################################*/

    @Nullable
    abstract Transaction obtainSessionTransaction();

    /*################################## blow private method ##################################*/


    private void assertChildDomain() {
        Transaction transaction = obtainSessionTransaction();
        if (transaction == null
                || transaction.isolation().level < Isolation.READ_COMMITTED.level) {
            throw new DomainUpdateException("Child domain update must in READ_COMMITTED transaction.");
        }
    }

    private void markRollbackOnlyForChildUpdate(SQLWrapper sqlWrapper) {
        Transaction transaction = obtainSessionTransaction();
        if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
            if (transaction != null) {
                transaction.markRollbackOnly();
            }
        }
    }

    private void markRollbackOnlyForChildInsert(List<SQLWrapper> sqlWrapperList) {
        Transaction transaction = obtainSessionTransaction();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
                if (transaction != null) {
                    transaction.markRollbackOnly();
                }
                break;
            }
        }
    }

    private SQLWrapper parseUpdate(Update update, Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.update(update, visible);
        if (sqlWrapper instanceof ChildSQLWrapper
                || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    private SQLWrapper parseDelete(Delete delete, Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        SQLWrapper sqlWrapper = this.dialect.delete(delete, visible);
        if (sqlWrapper instanceof ChildSQLWrapper
                || sqlWrapper instanceof ChildBatchSQLWrapper) {
            // 2. assert child update
            assertChildDomain();
        }
        return sqlWrapper;
    }

    private List<SQLWrapper> parseInsert(Insert insert, Visible visible) {
        if (this.readonly()) {
            throw new ReadOnlySessionException("current session/session transaction is read only.");
        }
        //1. parse update sql
        List<SQLWrapper> sqlWrapperList = this.dialect.subQueryInsert(insert, visible);
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof ChildSQLWrapper || sqlWrapper instanceof ChildBatchSQLWrapper) {
                assertChildDomain();
                break;
            }
        }
        return sqlWrapperList;
    }

}
