package io.army;

import io.army.sync.GenericSyncApiSessionFactory;

public interface TmSessionFactory extends GenericSyncApiSessionFactory, GenericTmSessionFactory {


    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        TmSession build() throws SessionException;

    }
}
