package io.army.reactive.executor;

import io.army.session.Session;

/**
 * <p>This interface representing RM(Resource Manager) {@link ReactiveStmtExecutor} that support XA transaction.
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @since 0.6.0
 */
public interface ReactiveRmExecutor extends ReactiveStmtExecutor,
        ReactiveStmtExecutor.XaTransactionSpec,
        Session.XaTransactionSupportSpec {


}
