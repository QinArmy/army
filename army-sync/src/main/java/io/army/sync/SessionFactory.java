package io.army.sync;

import io.army.GenericRmSessionFactory;
import io.army.SessionException;

/**
 * This interface
 */
public interface SessionFactory extends GenericSyncApiSessionFactory, GenericRmSessionFactory {


    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        SessionBuilder resetConnection(boolean reset);

        Session build() throws SessionException;

    }
}
