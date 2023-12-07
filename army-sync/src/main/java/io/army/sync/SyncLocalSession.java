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

    /**
     * <p>Start local/pseudo transaction.
     * <ul>
     *     <li>Local transaction is supported by database server.</li>
     *     <li>Pseudo transaction({@link TransactionOption#isolation()} is {@link Isolation#PSEUDO}) is supported only by army readonly session. Pseudo transaction is designed for some framework in readonly transaction,for example {@code org.springframework.transaction.PlatformTransactionManager}</li>
     * </ul>
     *
     * @param option non-null,if {@link TransactionOption#isolation()} is {@link Isolation#PSEUDO},then start pseudo transaction.
     * @param mode   non-null,
     *               <ul>
     *                  <li>{@link HandleMode#ERROR_IF_EXISTS} :  if session exists transaction then throw {@link SessionException}</li>
     *                  <li>{@link HandleMode#COMMIT_IF_EXISTS} :  if session exists transaction then commit existing transaction.</li>
     *                  <li>{@link HandleMode#ROLLBACK_IF_EXISTS} :  if session exists transaction then rollback existing transaction.</li>
     *               </ul>
     * @throws IllegalArgumentException                  throw when pseudo transaction {@link TransactionOption#isReadOnly()} is false.
     * @throws java.util.ConcurrentModificationException throw when concurrent control transaction
     * @throws SessionException                          throw when
     *                                                   <ul>
     *                                                       <li>session have closed</li>
     *                                                       <li>pseudo transaction and {@link #isReadonlySession()} is false</li>
     *                                                       <li>mode is {@link HandleMode#ERROR_IF_EXISTS} and {@link #hasTransactionInfo()} is true</li>
     *                                                       <li>mode is {@link HandleMode#COMMIT_IF_EXISTS} and commit failure</li>
     *                                                       <li>mode is {@link HandleMode#ROLLBACK_IF_EXISTS} and rollback failure</li>
     *                                                   </ul>
     */
    TransactionInfo startTransaction(TransactionOption option, HandleMode mode);

    void commit();


    @Nullable
    TransactionInfo commit(Function<Option<?>, ?> optionFunc);

    void commitIfExists();


    @Nullable
    TransactionInfo commitIfExists(Function<Option<?>, ?> optionFunc);

    void rollback();

    @Nullable
    TransactionInfo rollback(Function<Option<?>, ?> optionFunc);

    void rollbackIfExists();

    @Nullable
    TransactionInfo rollbackIfExists(Function<Option<?>, ?> optionFunc);


}
