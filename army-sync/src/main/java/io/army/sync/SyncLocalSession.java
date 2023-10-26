package io.army.sync;

import io.army.session.Option;
import io.army.session.TransactionInfo;
import io.army.session.TransactionOption;

import java.util.function.Function;

/**
 * <p>This interface representing blocking local session.
 *
 * @see SyncLocalSessionFactory
 * @since 1.0
 */
public interface SyncLocalSession extends SyncSession {

    @Override
    SyncLocalSessionFactory sessionFactory();

    TransactionInfo startTransaction(TransactionOption option);

    SyncLocalSession commit(Function<Option<?>, ?> optionFunc);

    SyncLocalSession rollback(Function<Option<?>, ?> optionFunc);


}
