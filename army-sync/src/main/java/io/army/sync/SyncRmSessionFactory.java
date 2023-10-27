package io.army.sync;

import io.army.session.SessionException;

/**
 * <p>This interface representing blocking RM(Resource Manager) {@link SyncRmSession} factory in XA transaction.
 * <p>The instance of This interface is created by {@link SyncRmSessionFactoryBuilder#build()}.
 *
 * @since 1.0
 */
interface SyncRmSessionFactory extends SyncSessionFactory {

    Builder builder();

    interface Builder extends SessionBuilderSpec<Builder, SyncRmSession> {

        @Override
        SyncRmSession build() throws SessionException;

    }

}
