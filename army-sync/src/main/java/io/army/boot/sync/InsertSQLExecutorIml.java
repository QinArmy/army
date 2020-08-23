package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.InsertRowsNotMatchException;
import io.army.beans.ObjectWrapper;
import io.army.codec.StatementType;
import io.army.dialect.Dialect;
import io.army.dialect.InsertException;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

final class InsertSQLExecutorIml extends SQLExecutorSupport implements InsertSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSQLExecutorIml.class);


    InsertSQLExecutorIml(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final void valueInsert(InnerGenericRmSession session, List<SQLWrapper> sqlWrapperList)
            throws InsertException {
        session.codecContextStatementType(StatementType.INSERT);
        try {
            final boolean showSQL = session.sessionFactory().showSQL();
            final Dialect dialect = session.dialect();
            for (SQLWrapper sqlWrapper : sqlWrapperList) {
                if (showSQL) {
                    LOG.info("army will execute insert sql:\n{}", dialect.showSQL(sqlWrapper));
                }
                if (sqlWrapper instanceof SimpleSQLWrapper) {
                    this.doExecuteSimple(session, (SimpleSQLWrapper) sqlWrapper);
                } else if (sqlWrapper instanceof ChildSQLWrapper) {
                    this.doExecuteChild(session, (ChildSQLWrapper) sqlWrapper, false);
                } else if (sqlWrapper instanceof BatchSimpleSQLWrapper) {

                    BatchSimpleSQLWrapper simpleSQLWrapper = (BatchSimpleSQLWrapper) sqlWrapper;
                    assertBatchResult(simpleSQLWrapper, doExecuteBatch(session, simpleSQLWrapper));

                } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                    this.doExecuteChildBatch(session, (ChildBatchSQLWrapper) sqlWrapper);
                } else {
                    throw createUnSupportedSQLWrapperException(sqlWrapper, "valueInsert");
                }
            }
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final int subQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException {
        session.codecContextStatementType(StatementType.INSERT);
        try {
            if (this.sessionFactory.showSQL()) {
                LOG.info("army will execute select sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            int insertRows;
            if (sqlWrapper instanceof SimpleSQLWrapper) {
                insertRows = doExecuteUpdate(session, (SimpleSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof ChildSQLWrapper) {
                insertRows = doExecuteChild(session, (ChildSQLWrapper) sqlWrapper, true);
            } else {
                throw createUnSupportedSQLWrapperException(sqlWrapper, "subQueryInsert");
            }
            return insertRows;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final long subQueryLargeInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException {
        session.codecContextStatementType(StatementType.INSERT);
        try {
            if (this.sessionFactory.showSQL()) {
                LOG.info("army will execute select sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            long insertRows;
            if (sqlWrapper instanceof SimpleSQLWrapper) {
                insertRows = doExecuteLargeUpdate(session, (SimpleSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof ChildSQLWrapper) {
                insertRows = doExecuteLargeSubQueryChild(session, (ChildSQLWrapper) sqlWrapper);
            } else {
                throw createUnSupportedSQLWrapperException(sqlWrapper, "subQueryLargeInsert");
            }
            return insertRows;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final <T> List<T> returningInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException {
        session.codecContextStatementType(StatementType.INSERT);
        try {
            if (this.sessionFactory.showSQL()) {
                LOG.info("army will execute select sql:\n{}", session.dialect().showSQL(sqlWrapper));
            }
            List<T> resultList;
            if (sqlWrapper instanceof SimpleSQLWrapper) {
                resultList = doExecuteSimpleReturning(session, (SimpleSQLWrapper) sqlWrapper, resultClass);
            } else if (sqlWrapper instanceof ChildSQLWrapper) {
                resultList = doExecuteInsertChildReturning(session, (ChildSQLWrapper) sqlWrapper, resultClass);
            } else {
                throw createUnSupportedSQLWrapperException(sqlWrapper, "returningInsert");
            }
            return resultList;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    /*################################## blow private method ##################################*/

    private void doExecuteSimple(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper) {
        int rows;
        rows = doExecuteUpdate(session, sqlWrapper);
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "SessionFactory[%s] sql[%s] multiInsert rows[%s] error."
                    , this.sessionFactory.name(), sqlWrapper.sql(), rows);
        }
    }

    private int doExecuteChild(InnerGenericRmSession session, ChildSQLWrapper childSQLWrapper
            , final boolean subQueryInsert) {

        int childRows, parentRows;
        // firstly,execute parent multiInsert sql
        parentRows = doExecuteUpdate(session, childSQLWrapper.parentWrapper());
        if (!subQueryInsert && parentRows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "SessionFactory[%s] sql[%s] multiInsert rows[%s] error."
                    , this.sessionFactory.name(), childSQLWrapper.parentWrapper(), parentRows);
        }
        // secondly, execute child multiInsert sql
        childRows = doExecuteUpdate(session, childSQLWrapper.childWrapper());
        if (parentRows != childRows) {
            throw new InsertRowsNotMatchException(
                    "SessionFactory[%s] child sql [%s] multiInsert rows[%s] and parent sql[%s] rows[%s] not match."
                    , this.sessionFactory.name(), childSQLWrapper.childWrapper().sql()
                    , childRows, childSQLWrapper.parentWrapper(), parentRows);
        }
        return childRows;
    }

    private long doExecuteLargeSubQueryChild(InnerGenericRmSession session, ChildSQLWrapper childSQLWrapper) {

        final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
        final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

        long childRows, parentRows;
        // firstly,execute parent multiInsert sql
        parentRows = doExecuteLargeUpdate(session, parentWrapper);

        // secondly, execute child multiInsert sql
        childRows = doExecuteLargeUpdate(session, childWrapper);
        if (parentRows != childRows) {
            throw new InsertRowsNotMatchException(
                    "child sql [%s] multiInsert rows[%s] and parent sql[%s] rows[%s] not match."
                    , childWrapper.sql(), childRows, parentWrapper.sql(), parentRows);
        }
        return childRows;
    }

    private <T> List<T> doExecuteInsertChildReturning(InnerGenericRmSession session, ChildSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        Map<Object, ObjectWrapper> beanWrapperMap;
        // firstly, execute child sql
        beanWrapperMap = doExecuteFirstReturning(session, sqlWrapper.parentWrapper(), resultClass);

        if (beanWrapperMap.isEmpty()) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "sql[%s] multiInsert rows[%s] error.", sqlWrapper.parentWrapper().sql(), beanWrapperMap.size());
        }
        // secondly, execute parent sql
        List<T> resultList;
        resultList = doExecuteSecondReturning(session, sqlWrapper.childWrapper(), beanWrapperMap);

        if (beanWrapperMap.size() != resultList.size()) {
            throw createBatchNotMatchException(sqlWrapper.parentWrapper().sql()
                    , sqlWrapper.childWrapper().sql(), beanWrapperMap.size(), resultList.size());
        }
        return resultList;
    }


    private void doExecuteChildBatch(InnerGenericRmSession session, ChildBatchSQLWrapper sqlWrapper) {

        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        int[] parenResult, childResult;
        // firstly, parent multiInsert sql
        parenResult = doExecuteBatch(session, parentWrapper);
        // secondly,child multiInsert sql
        childResult = doExecuteBatch(session, childWrapper);

        assertBatchChildResult(parentWrapper, childWrapper, parenResult, childResult);
    }

    private void assertBatchResult(BatchSimpleSQLWrapper sqlWrapper, int[] batchResult) {

        if (batchResult.length != sqlWrapper.paramGroupList().size()) {
            throw new InsertRowsNotMatchException("batch sql[%s] batch count , expected %s but %s ."
                    , sqlWrapper.sql(), sqlWrapper.paramGroupList().size(), batchResult.length);
        }
        for (int i = 0, updateRows; i < batchResult.length; i++) {
            updateRows = batchResult[i];
            if (updateRows != 1) {
                throw new InsertRowsNotMatchException(
                        "batch  sql[%s]  index[%s] actual row count[%s] not 1 ."
                        , sqlWrapper.sql(), i, updateRows);
            }
        }

    }

    private void assertBatchChildResult(BatchSimpleSQLWrapper parentWrapper, BatchSimpleSQLWrapper childWrapper
            , int[] parentResult, int[] childResult) {
        if (parentResult.length != childResult.length
                || childResult.length != childWrapper.paramGroupList().size()) {

            throw new InsertRowsNotMatchException(
                    "SessionFactory[%s] child sql[%s]  batch count[%s] and parent sql [%s] batch count[%s] not match."
                    , this.sessionFactory.name(), childWrapper.sql(), childResult.length
                    , parentWrapper.sql(), parentResult.length);
        }
        for (int i = 0, parentRows; i < parentResult.length; i++) {
            parentRows = parentResult[i];

            if (parentRows != 1) {
                throw new InsertRowsNotMatchException(
                        "SessionFactory[%s] batch  sql[%s]  index[%s] actual row count[%s] not 1 ."
                        , this.sessionFactory.name(), parentWrapper.sql(), i, parentRows);
            }
            if (parentRows != childResult[i]) {
                throw new InsertRowsNotMatchException(
                        "SessionFactory[%s] child sql[%s] index[%s] rows[%s] and parent sql [%s] rows[%s] not match."
                        , this.sessionFactory.name(), childWrapper.sql(), i, childResult[i]
                        , parentWrapper.sql(), parentRows);
            }

        }
    }


}
