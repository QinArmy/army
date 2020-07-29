package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.InsertRowsNotMatchException;
import io.army.beans.BeanWrapper;
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


    InsertSQLExecutorIml(InnerRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final void valueInsert(InnerSession session, List<SQLWrapper> sqlWrapperList)
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
                    throw createNotSupportedException(sqlWrapper, "valueInsert");
                }
            }
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final int subQueryInsert(InnerSession session, SQLWrapper sqlWrapper) throws InsertException {
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
                throw createNotSupportedException(sqlWrapper, "subQueryInsert");
            }
            return insertRows;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final long subQueryLargeInsert(InnerSession session, SQLWrapper sqlWrapper) throws InsertException {
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
                throw createNotSupportedException(sqlWrapper, "subQueryLargeInsert");
            }
            return insertRows;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    @Override
    public final <T> List<T> returningInsert(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
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
                throw createNotSupportedException(sqlWrapper, "returningInsert");
            }
            return resultList;
        } finally {
            session.codecContextStatementType(null);
        }
    }

    /*################################## blow private method ##################################*/

    private void doExecuteSimple(InnerSession session, SimpleSQLWrapper sqlWrapper) {
        int rows;
        rows = doExecuteUpdate(session, sqlWrapper);
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "SessionFactory[%s] sql[%s] multiInsert rows[%s] error."
                    , this.sessionFactory.name(), sqlWrapper.sql(), rows);
        }
    }

    private int doExecuteChild(InnerSession session, ChildSQLWrapper childSQLWrapper, final boolean subQueryInsert) {

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

    private long doExecuteLargeSubQueryChild(InnerSession session, ChildSQLWrapper childSQLWrapper) {

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

    private <T> List<T> doExecuteInsertChildReturning(InnerSession session, ChildSQLWrapper sqlWrapper
            , Class<T> resultClass) {

        Map<Object, BeanWrapper> beanWrapperMap;
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
            throw createBatchNotMatchException(this.sessionFactory.name(), sqlWrapper.parentWrapper().sql()
                    , sqlWrapper.childWrapper().sql(), beanWrapperMap.size(), resultList.size());
        }
        return resultList;
    }


    private void doExecuteChildBatch(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {

        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        Map<Integer, Integer> parenResultMap, childResultMap;
        // firstly, parent multiInsert sql
        parenResultMap = doExecuteBatch(session, parentWrapper);
        // secondly,child multiInsert sql
        childResultMap = doExecuteBatch(session, childWrapper);

        assertBatchChildResult(parentWrapper, childWrapper, parenResultMap, childResultMap);
    }

    private void assertBatchResult(BatchSimpleSQLWrapper sqlWrapper, Map<Integer, Integer> batchResultMap) {

        if (batchResultMap.size() != sqlWrapper.paramGroupList().size()) {
            throw new InsertRowsNotMatchException("batch sql[%s] batch count , expected %s but %s ."
                    , sqlWrapper.sql(), sqlWrapper.paramGroupList().size(), batchResultMap.size());
        }

        for (Map.Entry<Integer, Integer> e : batchResultMap.entrySet()) {
            if (e.getValue() != 1) {
                throw new InsertRowsNotMatchException(
                        "batch  sql[%s]  index[%s] actual row count[%s] not 1 ."
                        , sqlWrapper.sql(), e.getKey(), e.getValue());
            }
        }

    }

    private void assertBatchChildResult(BatchSimpleSQLWrapper parentWrapper, BatchSimpleSQLWrapper childWrapper
            , Map<Integer, Integer> parenResultMap, Map<Integer, Integer> childResultMap) {
        if (parenResultMap.size() != childResultMap.size()
                || childResultMap.size() != childWrapper.paramGroupList().size()) {

            throw new InsertRowsNotMatchException(
                    "SessionFactory[%s] child sql[%s]  batch count[%s] and parent sql [%s] batch count[%s] not match."
                    , this.sessionFactory.name(), childWrapper.sql(), childResultMap.size()
                    , parentWrapper.sql(), parenResultMap.size());
        }
        for (Map.Entry<Integer, Integer> p : parenResultMap.entrySet()) {
            Integer parentRow = p.getValue();
            if (parentRow != 1) {
                throw new InsertRowsNotMatchException(
                        "SessionFactory[%s] batch  sql[%s]  index[%s] actual row count[%s] not 1 ."
                        , this.sessionFactory.name(), parentWrapper.sql(), p.getKey(), parentRow);
            }
            if (!parentRow.equals(childResultMap.get(p.getKey()))) {
                throw new InsertRowsNotMatchException(
                        "SessionFactory[%s] child sql[%s] index[%s] rows[%s] and parent sql [%s] rows[%s] not match."
                        , this.sessionFactory.name(), childWrapper.sql(), p.getKey(), childResultMap.get(p.getKey())
                        , parentWrapper.sql(), parentRow);
            }
        }

    }


}
