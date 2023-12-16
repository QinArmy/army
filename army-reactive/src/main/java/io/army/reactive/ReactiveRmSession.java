package io.army.reactive;

import io.army.session.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>This interface representing reactive RM(Resource Manager) session in XA transaction.
 * <p>This interface extends {@link Session} for support XA interface based on
 * the X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).<br/>
 * This document is published by The Open Group and available at
 * <a href="http://www.opengroup.org/public/pubs/catalog/c193.htm">The XA Specification</a>,
 * here ,you can download the pdf about The XA Specification.
 *
 * @since 0.6.0
 */
public interface ReactiveRmSession extends ReactiveSession, RmSession {


    Mono<TransactionInfo> start(Xid xid);

    Mono<TransactionInfo> start(Xid xid, int flags);

    Mono<TransactionInfo> start(Xid xid, int flags, TransactionOption option);

    Mono<ReactiveRmSession> end(Xid xid);

    Mono<ReactiveRmSession> end(Xid xid, int flags);

    Mono<ReactiveRmSession> end(Xid xid, int flags, Function<Option<?>, ?> optionFunc);


    Mono<Integer> prepare(Xid xid);

    Mono<Integer> prepare(Xid xid, Function<Option<?>, ?> optionFunc);


    Mono<ReactiveRmSession> commit(Xid xid);

    Mono<ReactiveRmSession> commit(Xid xid, int flags);

    Mono<ReactiveRmSession> commit(Xid xid, int flags, Function<Option<?>, ?> optionFunc);

    Mono<ReactiveRmSession> rollback(Xid xid);

    Mono<ReactiveRmSession> rollback(Xid xid, Function<Option<?>, ?> optionFunc);

    Mono<ReactiveRmSession> forget(Xid xid);

    Mono<ReactiveRmSession> forget(Xid xid, Function<Option<?>, ?> optionFunc);

    Flux<Optional<Xid>> recover(int flags);

    Flux<Optional<Xid>> recover(int flags, Function<Option<?>, ?> optionFunc);


}
