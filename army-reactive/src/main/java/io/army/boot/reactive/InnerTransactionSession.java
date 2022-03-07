package io.army.boot.reactive;

import io.army.reactive.Session;
import io.army.tx.reactive.GenericReactiveTransaction;
import io.jdbd.session.DatabaseSession;
import reactor.core.publisher.Mono;


interface InnerTransactionSession extends Session {


    Mono<Void> closeTransaction(GenericReactiveTransaction transaction);

    DatabaseSession databaseSession(ReactiveLocalTransaction sessionTransaction);

}
