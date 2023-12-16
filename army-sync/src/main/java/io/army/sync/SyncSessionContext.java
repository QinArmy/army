package io.army.sync;

import io.army.session.NoCurrentSessionException;

/**
 * <p>This interface representing current {@link SyncSession} context.
 * <p>This interface is designed for some framework,for example {@code  org.springframework.transaction.PlatformTransactionManager}
 *
 * @since 0.6.0
 */
public interface SyncSessionContext {

    SyncSessionFactory sessionFactory();

    <T extends SyncSessionFactory> T sessionFactory(Class<T> factoryClass);

    boolean hasCurrentSession();

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws NoCurrentSessionException Typically indicates an issue
     *                                   locating or creating the current session.
     */
    SyncSession currentSession() throws NoCurrentSessionException;

    <T extends SyncSession> T currentSession(Class<T> sessionClass) throws NoCurrentSessionException;


}
