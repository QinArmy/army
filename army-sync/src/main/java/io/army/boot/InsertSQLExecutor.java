package io.army.boot;

import io.army.dialect.InsertException;
import io.army.generator.PostFieldGenerator;
import io.army.wrapper.DomainBatchSQLWrapper;
import io.army.wrapper.SQLWrapper;

import java.util.List;

/**
 * @see PostFieldGenerator
 */
interface InsertSQLExecutor extends DMLSQLExecutor {


    List<Integer> insert(InnerSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    List<Integer> batchInsert(InnerSession session, List<DomainBatchSQLWrapper> batchSQLWrapperList);


    static InsertSQLExecutor build() {
        return InsertSQLExecutorIml.INSTANCE;
    }

}
