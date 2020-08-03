package io.army.sync;

import io.army.GenericTmSessionFactory;
import io.army.SessionException;


public interface TmSessionFactory extends GenericSyncApiSessionFactory, GenericTmSessionFactory {


    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        TmSession build() throws SessionException;

    }
}
