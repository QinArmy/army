package io.army.sync;

import io.army.session.*;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing local {@link SyncLocalSession} that support database local transaction.
 * <p>The instance of this interface is created by {@link SyncSessionFactory.LocalSessionBuilder}.
 *
 * @see SyncSessionFactory
 * @since 1.0
 */
public interface SyncLocalSession extends SyncSession, LocalSession {


    TransactionInfo startTransaction();

    TransactionInfo startTransaction(TransactionOption option);

    TransactionInfo startTransaction(TransactionOption option, HandleMode mode);

    void commit();


    @Nullable
    TransactionInfo commit(Function<Option<?>, ?> optionFunc);

    @Nullable
    TransactionInfo rollback(Function<Option<?>, ?> optionFunc);


    void rollback();


}
