package io.army.boot.reactive;

import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.wrapper.SimpleSQLWrapper;
import reactor.core.publisher.Flux;

final class ReactiveSelectSQLExecutorImpl extends ReactiveSQLExecutorSupport implements ReactiveSelectSQLExecutor {

    ReactiveSelectSQLExecutorImpl(GenericReactiveRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final <T> Flux<T> select(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper, Class<T> resultClass) {
        return doExecuteSimpleQuery(session, sqlWrapper, resultClass);
    }
}
