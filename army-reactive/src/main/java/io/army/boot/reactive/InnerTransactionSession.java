package io.army.boot.reactive;

import io.army.reactive.ReactiveSession;
import io.army.tx.reactive.GenericReactiveTransaction;
import io.jdbd.DatabaseSession;
import io.jdbd.ReactiveSQLException;
import reactor.core.publisher.Mono;


interface InnerTransactionSession extends ReactiveSession {


    Mono<Void> closeTransaction(GenericReactiveTransaction transaction) throws ReactiveSQLException;

    DatabaseSession databaseSession(ReactiveLocalTransaction sessionTransaction);

}
