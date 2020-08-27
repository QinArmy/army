package io.army.boot.reactive;

import io.army.reactive.BaseReactiveSession;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import reactor.core.publisher.Mono;


interface InnerGenericRmSession extends BaseReactiveSession {

    Mono<PreparedStatement> createPreparedStatement(String sql) throws ReactiveSQLException;

}
