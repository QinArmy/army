package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.HandleMode;
import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session.TransactionOption;

import java.util.function.Function;

/**
 * <p>This interface representing local {@link SyncLocalSession} that support database local transaction.
 * <p>The instance of this interface is created by {@link SyncLocalSessionFactory.SessionBuilder}.
 *
 * @see SyncLocalSessionFactory
 * @since 1.0
 */
public interface SyncLocalSession extends SyncSession {

    @Override
    SyncLocalSessionFactory sessionFactory();

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
