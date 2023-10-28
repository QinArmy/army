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
     * <p>The status will clear after rollback or start new transaction
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     *
     * @see #isRollbackOnly()
     */
    void markRollbackOnly();

}
