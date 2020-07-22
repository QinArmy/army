package io.army.sync;

import io.army.SessionException;

/**
 *
 */
public interface SessionFactory extends GenericSyncApiSessionFactory {


    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        SessionBuilder resetConnection(boolean reset);

        Session build() throws SessionException;

    }
}
