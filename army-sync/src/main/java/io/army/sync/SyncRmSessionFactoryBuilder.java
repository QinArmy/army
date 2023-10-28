package io.army.sync;

import io.army.session.FactoryBuilderSpec;
import io.army.session.SessionFactoryException;

/**
 * <p>This interface representing the builder of {@link SyncRmSessionFactory}.
 * <p>The instance of This interface is created by {@link #builder()}.
 *
 * @since 1.0
 */
public interface SyncRmSessionFactoryBuilder extends FactoryBuilderSpec<SyncRmSessionFactoryBuilder, SyncRmSessionFactory> {

    @Override
    SyncRmSessionFactory build() throws SessionFactoryException;

    static SyncRmSessionFactoryBuilder builder() {
        return ArmySyncRmFactoryBuilder.create();
    }


}
