package io.army.boot;

import io.army.Session;
import io.army.beans.BeanWrapper;
import io.army.dialect.InsertException;
import io.army.dialect.SQLWrapper;

import java.util.List;

/**
 * @see io.army.generator.PostMultiGenerator
 */
interface InsertSQLExecutor extends DMLSQLExecutor {


    void executeInsert(InnerSession session, List<SQLWrapper> sqlWrapperList, BeanWrapper beanWrapper)
            throws InsertException;

    static InsertSQLExecutor build() {
        return InsertSQLExecutorIml.INSTANCE;
    }

}
