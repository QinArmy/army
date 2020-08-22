package io.army.boot.reactive;

import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.wrapper.SimpleSQLWrapper;
import reactor.core.publisher.Flux;

public interface ReactiveSelectSQLExecutor {

    <T> Flux<T> select(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper, Class<T> resultClass);

    static ReactiveSelectSQLExecutor build(GenericReactiveRmSessionFactory sessionFactory) {
        return new ReactiveSelectSQLExecutorImpl(sessionFactory);
    }

}
