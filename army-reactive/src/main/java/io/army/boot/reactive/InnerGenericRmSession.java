package io.army.boot.reactive;

import io.army.GenericSession;
import io.jdbd.PreparedStatement;
import io.jdbd.ReactiveSQLException;
import reactor.core.publisher.Mono;

/**
 * This interface design for below:
 * <ul>
 *     <li>{@link InsertSQLExecutor}</li>
 *     <li>{@link SelectSQLExecutor}</li>
 *     <li>{@link UpdateSQLExecutor}</li>
 * </ul>
 */
interface InnerGenericRmSession extends GenericSession {

    Mono<PreparedStatement> createPreparedStatement(String sql) throws ReactiveSQLException;

}
