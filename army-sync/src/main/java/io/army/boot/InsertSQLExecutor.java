package io.army.boot;

import io.army.dialect.InsertException;
import io.army.wrapper.SQLWrapper;

import java.util.List;

/**
 *
 */
interface InsertSQLExecutor {


    List<Integer> multiInsert(InnerSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    List<Integer> batchInsert(InnerSession session, List<BatchSQLWrapper> sqlWrapperList);


    static InsertSQLExecutor build(InnerSessionFactory sessionFactory) {
        return new InsertSQLExecutorIml(sessionFactory);
    }

}
