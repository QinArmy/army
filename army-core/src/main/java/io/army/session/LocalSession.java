package io.army.session;

/**
 * <p>This interface representing local {@link Session} that support database local transaction.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code io.army.sync.SyncLocalSession}</li>
 *     <li>{@code io.army.reactive.ReactiveLocalSession}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface LocalSession extends Session {


    /**
     * <p>Mark local session rollback only
     * <p>More info ,see {@link #isRollbackOnly()}
     * <p><strong>NOTE</strong> : This method don't check session whether closed or not.
     *
     * @see #isRollbackOnly()
     */
    void markRollbackOnly();


    /**
     * @throws IllegalArgumentException throw {@link TransactionOption#isolation()} is null.
     * @throws SessionException         throw when
     *                                  <ul>
     *                                      <li>session have closed</li>
     *                                      <li>{@link #isReadonlySession()} return false</li>
     *                                      <li>{@link #hasTransactionInfo()} return true and mode is {@link HandleMode#ERROR_IF_EXISTS}</li>
     *                                      <li>{@link #hasTransactionInfo()} return true and mode is {@link HandleMode#COMMIT_IF_EXISTS} and timeout</li>
     *                                  </ul>
     */
    TransactionInfo pseudoTransaction(TransactionOption option, HandleMode mode);


}
