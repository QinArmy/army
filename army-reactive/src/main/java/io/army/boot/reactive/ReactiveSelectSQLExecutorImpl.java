package io.army.boot.reactive;

import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.wrapper.SimpleSQLWrapper;
import io.jdbd.PreparedStatement;
import reactor.core.publisher.Flux;

final class ReactiveSelectSQLExecutorImpl extends ReactiveSQLExecutorSupport implements ReactiveSelectSQLExecutor {

    ReactiveSelectSQLExecutorImpl(GenericReactiveRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final <T> Flux<T> select(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {
        PreparedStatement st = session.createPreparedStatement(sqlWrapper.sql());

        return null;
    }
}
