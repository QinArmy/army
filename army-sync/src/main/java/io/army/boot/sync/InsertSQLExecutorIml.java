package io.army.boot.sync;

import io.army.dialect.InsertException;
import io.army.util.Assert;
import io.army.wrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BiFunction;

final class InsertSQLExecutorIml extends SQLExecutorSupport implements InsertSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSQLExecutorIml.class);


    InsertSQLExecutorIml(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final void valueInsert(InnerGenericRmSession session, List<SQLWrapper> sqlWrapperList) {
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
            if (sqlWrapper instanceof SimpleSQLWrapper) {
                SimpleSQLWrapper simpleSQLWrapper = (SimpleSQLWrapper) sqlWrapper;
                int rows;
                ///1. execute insert sql
                rows = doExecuteUpdate(session, simpleSQLWrapper, this::integerUpdate);
                //2. assert insert rows equals 1 .
                assertValueInsertResult(rows, simpleSQLWrapper);
            } else if (sqlWrapper instanceof ChildSQLWrapper) {
                final ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
                final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
                int rows;
                //1. execute parent insert sql
                rows = doExecuteUpdate(session, parentWrapper, this::integerUpdate);
                //2. assert parent insert rows equals 1 .
                assertValueInsertResult(rows, parentWrapper);
                final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
                //3. execute child insert sql
                rows = doExecuteUpdate(session, childWrapper, this::integerUpdate);
                //4. assert child insert rows equals 1 .
                assertValueInsertResult(rows, childWrapper);
            } else if (sqlWrapper instanceof BatchSimpleSQLWrapper) {
                BatchSimpleSQLWrapper simpleSQLWrapper = (BatchSimpleSQLWrapper) sqlWrapper;
                //1. assert StatementType for integerBatchUpdate function
                Assert.isTrue(simpleSQLWrapper.statementType().insertStatement(), "sqlWrapper error");
                // 2. execute batch insert sql
                doExecuteBatch(session, simpleSQLWrapper, this::integerBatchUpdate);

            } else if (sqlWrapper instanceof ChildBatchSQLWrapper) {
                final ChildBatchSQLWrapper childSQLWrapper = (ChildBatchSQLWrapper) sqlWrapper;
                final BatchSimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
                List<Integer> parentList, childList;
                //1. assert StatementType for integerBatchUpdate function
                Assert.isTrue(parentWrapper.statementType().insertStatement(), "sqlWrapper error");
                //2. execute parent batch insert sql
                parentList = doExecuteBatch(session, parentWrapper, this::integerBatchUpdate);

                final BatchSimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
                //3. assert StatementType for integerBatchUpdate function
                Assert.isTrue(childWrapper.statementType().insertStatement(), "sqlWrapper error");
                //4. execute child batch insert sql
                childList = doExecuteBatch(session, childWrapper, this::integerBatchUpdate);
                if (childList.size() != parentList.size()) {
                    throw createBatchChildInsertNotMatchException(parentList.size(), childList.size(), childWrapper);
                }
            } else {
                throw createUnSupportedSQLWrapperException(sqlWrapper, "valueInsert");
            }
        }
    }


    @Override
    public final int subQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException {
        return internalSubQueryInsert(session, sqlWrapper, this::integerUpdate, "subQueryInsert");
    }

    @Override
    public final long subQueryLargeInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException {
        return internalSubQueryInsert(session, sqlWrapper, this::longUpdate, "subQueryLargeInsert");
    }

    @Override
    public final <T> List<T> returningInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException {
        return doExecuteReturning(session, sqlWrapper, resultClass, false, "returningInsert");
    }

    /*################################## blow private method ##################################*/


    /**
     * @param executeFunction execute update method ,must be below:
     *                        <ul>
     *                          <li>{@link #integerUpdate(PreparedStatement, SimpleSQLWrapper)}</li>
     *                          <li>{@link #longUpdate(PreparedStatement, SimpleSQLWrapper)}</li>
     *                        </ul>
     * @param <N>             result typed of update rows ,must be  {@link Integer} or {@link Long}
     * @return {@code Integer or Long}
     */
    private <N extends Number> N internalSubQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , BiFunction<PreparedStatement, SimpleSQLWrapper, N> executeFunction, String methodName) {
        N rows;
        if (sqlWrapper instanceof SimpleSQLWrapper) {
            rows = doExecuteUpdate(session, (SimpleSQLWrapper) sqlWrapper, executeFunction);
        } else if (sqlWrapper instanceof ChildSQLWrapper) {
            final ChildSQLWrapper childSQLWrapper = (ChildSQLWrapper) sqlWrapper;
            final SimpleSQLWrapper parentWrapper = childSQLWrapper.parentWrapper();
            N parentRows;
            //1. execute parent sub query insert sql
            parentRows = doExecuteUpdate(session, parentWrapper, executeFunction);
            //2. assert parent insert rows equals 1 .
            if (parentRows.longValue() > 0L) {
                final SimpleSQLWrapper childWrapper = childSQLWrapper.childWrapper();
                //2. execute child sub query insert sql
                rows = doExecuteUpdate(session, childWrapper, executeFunction);
                if (!rows.equals(parentRows)) {
                    throw createChildSubQueryInsertNotMatchException(parentRows, rows, childWrapper);
                }
            } else {
                rows = parentRows;
            }
        } else {
            throw createUnSupportedSQLWrapperException(sqlWrapper, methodName);
        }
        return rows;
    }

    private void assertValueInsertResult(int insertRows, GenericSimpleWrapper sqlWrapper) {
        if (insertRows != 1) {
            throw createValueInsertException(insertRows, sqlWrapper);
        }
    }


}
