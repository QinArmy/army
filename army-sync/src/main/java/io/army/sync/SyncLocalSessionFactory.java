package io.army.sync;

import io.army.session.SessionException;
import io.army.session.SessionFactoryException;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 */
public interface SyncLocalSessionFactory extends SyncSessionFactory {


    SessionContext currentSessionContext() throws SessionFactoryException;

    SessionBuilder builder();


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, SyncLocalSession> {

        @Override
        SyncLocalSession build() throws SessionException;


    }


}
