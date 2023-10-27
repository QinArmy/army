package io.army.sync;

import io.army.session.SessionException;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 */
public interface SyncLocalSessionFactory extends SyncSessionFactory {


    SessionBuilder builder();


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, SyncLocalSession> {

        @Override
        SyncLocalSession build() throws SessionException;

    }


}
