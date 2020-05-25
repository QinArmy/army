package io.army.boot;

import io.army.ErrorCode;
import io.army.InsertRowsNotMatchException;
import io.army.dialect.Dialect;
import io.army.dialect.InsertException;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class InsertSQLExecutorIml extends SQLExecutorSupport implements InsertSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSQLExecutorIml.class);


    InsertSQLExecutorIml(InnerSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final void insert(InnerSession session, List<SQLWrapper> sqlWrapperList)
            throws InsertException {
        final boolean showSQL = session.sessionFactory().showSQL();
        final Dialect dialect = session.dialect();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (showSQL) {
                LOG.info("{}", dialect.showSQL(sqlWrapper));
            }
            if (sqlWrapper instanceof SimpleSQLWrapper) {
                doExecuteSimple(session, (SimpleSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof ChildSQLWrapper) {
                doExecuteChild(session, (ChildSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
                doExecuteSimpleBatch(session, (BatchSimpleSQLWrapper) sqlWrapper);
            } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                doExecuteChildBatch(session, (ChildBatchSQLWrapper) sqlWrapper);
            } else {
                throw new IllegalArgumentException(String.format("%s supported by multiInsert", sqlWrapper));
            }
        }
    }

    @Override
    public <T> List<T> returningInsert(InnerSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException {
        return null;
    }

    /*################################## blow private method ##################################*/

    private void doExecuteSimple(InnerSession session, SimpleSQLWrapper sqlWrapper) {
        int rows;
        rows = doExecuteUpdate(session, sqlWrapper);
        if (rows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "sql[%s] multiInsert rows[%s] error.", sqlWrapper.sql(), rows);
        }
    }

    private void doExecuteChild(InnerSession session, ChildSQLWrapper childSQLWrapper) {

        final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
        final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();

        int childRows, parentRows;
        // firstly,execute parent multiInsert sql
        parentRows = doExecuteUpdate(session, parentWrapper);

        if (parentRows != 1) {
            throw new InsertException(ErrorCode.INSERT_ERROR
                    , "sql[%s] multiInsert rows[%s] error.", parentWrapper.sql(), parentRows);
        }
        // secondly, execute child multiInsert sql
        childRows = doExecuteUpdate(session, childWrapper);

        if (parentRows != childRows) {
            throw new InsertRowsNotMatchException(
                    "child sql [%s] multiInsert rows[%s] and parent sql[%s] rows[%s] not match."
                    , childWrapper.sql(), childRows, parentWrapper.sql(), parentRows);
        }
    }

    private void doExecuteSimpleBatch(InnerSession session, BatchSimpleSQLWrapper sqlWrapper) {
        assertBatchResult(sqlWrapper, doExecuteBatch(session, sqlWrapper));
    }

    private void doExecuteChildBatch(InnerSession session, ChildBatchSQLWrapper sqlWrapper) {

        final BatchSimpleSQLWrapper parentWrapper = sqlWrapper.parentWrapper();
        final BatchSimpleSQLWrapper childWrapper = sqlWrapper.childWrapper();

        int[] parentRows, childRows;
        // firstly, parent multiInsert sql
        parentRows = doExecuteBatch(session, parentWrapper);

        // secondly,child multiInsert sql
        childRows = doExecuteBatch(session, childWrapper);

        assertBatchChildResult(parentWrapper, childWrapper, parentRows, childRows);
    }



}
