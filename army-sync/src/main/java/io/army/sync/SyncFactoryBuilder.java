package io.army.sync;

import io.army.session.FactoryBuilderSpec;
import io.army.session.SessionFactoryException;

/**
 * <p>This interface representing the builder of {@link SyncSessionFactory}.
 * <p>The instance of This interface is created by {@link #builder()}.
 *
 * @since 0.6.0
 */
public interface SyncFactoryBuilder extends FactoryBuilderSpec<SyncFactoryBuilder, SyncSessionFactory> {


    @Override
    SyncSessionFactory build() throws SessionFactoryException;


    static SyncFactoryBuilder builder() {
        return ArmySyncFactoryBuilder.create();
    }


}
