package io.army.sync.executor;

import javax.annotation.Nullable;

import io.army.session.HandleMode;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session.TransactionOption;

import java.util.function.Function;

/**
 * <p>The instance of this interface is created by {@link SyncStmtExecutorFactory#localExecutor(String)}.
 *
 * @since 1.0
 */
public interface SyncLocalStmtExecutor extends SyncStmtExecutor, SyncStmtExecutor.LocalTransactionSpec {

    TransactionInfo startTransaction(TransactionOption option, HandleMode mode);

    @Nullable
    TransactionInfo commit(Function<Option<?>, ?> optionFunc);

    @Nullable
    TransactionInfo rollback(Function<Option<?>, ?> optionFunc);


}
