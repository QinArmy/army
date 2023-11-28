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


    /**
     * <p>Start pseudo transaction that don't start real local transaction.
     * <p>Pseudo transaction is designed for some framework in readonly transaction,for example {@code org.springframework.transaction.TransactionManager}.
     *
     * @throws IllegalArgumentException                  throw when
     *                                                   <ul>
     *                                                       <li>{@link TransactionOption#isolation()} isn't {@link Isolation#PSEUDO}</li>
     *                                                       <li>{@link TransactionOption#isReadOnly()} is false</li>
     *                                                   </ul>
     * @throws java.util.ConcurrentModificationException throw when concurrent control transaction
     * @throws SessionException                          throw when
     *                                                   <ul>
     *                                                       <li>session have closed</li>
     *                                                       <li>{@link #isReadonlySession()} is false</li>
     *                                                       <li>mode is {@link HandleMode#ERROR_IF_EXISTS} and {@link #hasTransactionInfo()} is true</li>
     *                                                       <li>mode is {@link HandleMode#COMMIT_IF_EXISTS} and commit failure</li>
     *                                                       <li>mode is {@link HandleMode#ROLLBACK_IF_EXISTS} and rollback failure</li>
     *                                                   </ul>
     */
    TransactionInfo pseudoTransaction(TransactionOption option, HandleMode mode);


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
