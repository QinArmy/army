package io.army.reactive;

import io.army.session.*;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>This interface representing reactive RM(Resource Manager) session in XA transaction.
 *
 * <p>This interface extends {@link Session} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).<br/>
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 *
 * @since 1.0
 */
public interface ReactiveRmSession extends ReactiveSession, RmSession {

    @Override
    ReactiveRmSessionFactory sessionFactory();

    Mono<TransactionInfo> start(Xid xid, int flags, TransactionOption option);

    Mono<ReactiveRmSession> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    Mono<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc);

    Mono<ReactiveRmSession> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    Mono<ReactiveRmSession> rollback(Xid xid, Function<Option<?>, ?> optionFunc);


    Mono<ReactiveRmSession> forget(Xid xid, Function<Option<?>, ?> optionFunc);

    Mono<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc);


}
