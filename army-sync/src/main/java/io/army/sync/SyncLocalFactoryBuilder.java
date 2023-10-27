package io.army.sync;

import io.army.session.FactoryBuilderSpec;
import io.army.session.SessionFactoryException;

/**
 * <p>This interface representing the builder of {@link SyncLocalSessionFactory}.
 *
 * @since 1.0
 */
public interface SyncLocalFactoryBuilder extends FactoryBuilderSpec<SyncLocalFactoryBuilder, SyncLocalSessionFactory> {


    @Override
    SyncLocalSessionFactory build() throws SessionFactoryException;


    static SyncLocalFactoryBuilder builder() {
        return new LocalSessionFactoryBuilder();
    }


}
