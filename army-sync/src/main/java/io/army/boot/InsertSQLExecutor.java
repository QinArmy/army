package io.army.boot;

import io.army.dialect.BatchSQLWrapper;
import io.army.dialect.InsertException;
import io.army.dialect.SQLWrapper;

import java.util.List;

/**
 * @see io.army.generator.PostMultiGenerator
 */
interface InsertSQLExecutor extends DMLSQLExecutor {


    List<Integer> insert(InnerSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    List<Integer> batchInsert(InnerSession session, List<BatchSQLWrapper> batchSQLWrapperList);


    static InsertSQLExecutor build() {
        return InsertSQLExecutorIml.INSTANCE;
    }

}
