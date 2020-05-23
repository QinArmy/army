package io.army.boot;

import io.army.SessionFactory;
import io.army.wrapper.SQLWrapper;

import java.util.List;

/**
 * @see InsertSQLExecutor
 * @see UpdateSQLExecutor
 * @see SelectSQLExecutor
 */
interface DeleteSQLExecutor {

    int delete(InnerSession session, SQLWrapper sqlWrapper);

    List<Integer> batchDelete(InnerSession session, SQLWrapper sqlWrapper);

    <T> List<T> returningDelete(InnerSession session, SQLWrapper sqlWrapper);

    static DeleteSQLExecutor build(SessionFactory sessionFactory) {
        return new DeleteSQLExecutorImpl(sessionFactory);
    }
}
