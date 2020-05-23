package io.army.boot;

import io.army.SessionFactory;
import io.army.wrapper.SQLWrapper;

import java.util.List;

final class DeleteSQLExecutorImpl implements DeleteSQLExecutor {

    private final SessionFactory sessionFactory;

    DeleteSQLExecutorImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public int delete(InnerSession session, SQLWrapper sqlWrapper) {
        return 0;
    }

    @Override
    public List<Integer> batchDelete(InnerSession session, SQLWrapper sqlWrapper) {
        return null;
    }

    @Override
    public <T> List<T> returningDelete(InnerSession session, SQLWrapper sqlWrapper) {
        return null;
    }
}
