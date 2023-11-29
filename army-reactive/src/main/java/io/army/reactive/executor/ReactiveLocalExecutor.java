package io.army.reactive.executor;


/**
 * <p>This interface representing local {@link ReactiveStmtExecutor} that support local transaction.
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @since 1.0
 */
public interface ReactiveLocalExecutor extends ReactiveStmtExecutor, ReactiveStmtExecutor.LocalTransactionSpec {


}
