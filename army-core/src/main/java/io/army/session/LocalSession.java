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

}
