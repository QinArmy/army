package io.army.sync;

import io.army.session.SessionException;
import io.army.session.SessionFactoryException;

/**
 * <p>This interface representing the factory of {@link SyncLocalSession}.
 * <p>The instance of this interface is created by {@link SyncLocalFactoryBuilder#build()} .
 *
 * @see SyncLocalFactoryBuilder
 * @since 1.0
 */
public interface SyncLocalSessionFactory extends SyncSessionFactory {


    /**
     * Create the builder of {@link SyncLocalSession}
     *
     * @throws SessionFactoryException throw when factory have closed
     */
    SessionBuilder builder() throws SessionFactoryException;


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, SyncLocalSession> {

        @Override
        SyncLocalSession build() throws SessionException;

    }


}
