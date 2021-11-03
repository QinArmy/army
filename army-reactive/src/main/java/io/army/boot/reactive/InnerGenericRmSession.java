package io.army.boot.reactive;

import io.army.GenericSession;
import reactor.core.publisher.Mono;

import java.sql.PreparedStatement;

/**
 * This interface design for below:
 * <ul>
 *     <li>{@link InsertSQLExecutor}</li>
 *     <li>{@link SelectSQLExecutor}</li>
 *     <li>{@link UpdateSQLExecutor}</li>
 * </ul>
 */
interface InnerGenericRmSession extends GenericSession {

    Mono<PreparedStatement> createPreparedStatement(String sql);

}
