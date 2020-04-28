package io.army.boot;

import io.army.dialect.BatchSQLWrapper;
import io.army.dialect.InsertException;
import io.army.dialect.SQLWrapper;

import java.util.List;

/**
 * @see io.army.generator.PostMultiGenerator
 */
interface InsertSQLExecutor extends DMLSQLExecutor {


    void insert(InnerSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    void batchInsert(InnerSession session, List<BatchSQLWrapper> batchSQLWrapperList);


    static InsertSQLExecutor build() {
        return InsertSQLExecutorIml.INSTANCE;
    }

}
