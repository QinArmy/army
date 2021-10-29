package io.army.sync;

import io.army.wrapper.SimpleSQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class SelectSQLExecutorImpl extends SQLExecutorSupport implements SelectSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SelectSQLExecutorImpl.class);


    SelectSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @Override
    public final <T> List<T> select(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {
        return doExecuteSimpleQuery(session, sqlWrapper, resultClass);
    }

    /*################################## blow private method ##################################*/


}
