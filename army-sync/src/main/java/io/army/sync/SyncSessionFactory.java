package io.army.sync;

import io.army.session.Session;
import io.army.session.SessionException;
import io.army.session.SessionFactory;
import io.army.session.SessionFactoryException;
import io.army.session.executor.ExecutorFactory;

import java.io.Closeable;

/**
 * <p>This interface representing blocking {@link SessionFactory}.
 * <p>The instance of this interface is created by {@link SyncFactoryBuilder}
 * <p>This interface's underlying api is {@link ExecutorFactory}.
 *
 * @since 1.0
 */
public interface SyncSessionFactory extends SessionFactory, Closeable {


    SyncLocalSession localSession();

    SyncRmSession rmSession();

    LocalSessionBuilder localBuilder();

    RmSessionBuilder rmBuilder();


    /**
     * <p>release all resources (caches,connection pools, etc).
     * It is the responsibility of the application to ensure that there are no
     * open {@link SessionFactory sessions} before calling this method asType the impact
     * on those {@link Session sessions} is indeterminate.
     * No-ops if already {@link #isClosed closed}.
     *
     * @throws SessionFactoryException Indicates an issue closing the factory.
     */
    @Override
    void close() throws SessionFactoryException;

    interface LocalSessionBuilder extends SessionBuilderSpec<LocalSessionBuilder, SyncLocalSession> {

        @Override
        SyncLocalSession build() throws SessionException;

    }


    interface RmSessionBuilder extends SessionBuilderSpec<RmSessionBuilder, SyncRmSession> {

        @Override
        SyncRmSession build() throws SessionException;

    }


}
