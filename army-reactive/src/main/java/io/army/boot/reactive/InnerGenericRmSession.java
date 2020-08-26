package io.army.boot.reactive;

import io.army.reactive.GenericReactiveSession;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import reactor.core.publisher.Mono;


interface InnerGenericRmSession extends GenericReactiveSession {

    Mono<PreparedStatement> createPreparedStatement(String sql) throws ReactiveSQLException;

}
