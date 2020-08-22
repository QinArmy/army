package io.army.boot.reactive;

import io.army.codec.CodecContext;
import io.army.reactive.GenericReactiveRmSession;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import reactor.core.publisher.Mono;


interface InnerGenericRmSession extends GenericReactiveRmSession {

    Mono<PreparedStatement> createPreparedStatement(String sql) throws ReactiveSQLException;

    CodecContext codecContext();
}
