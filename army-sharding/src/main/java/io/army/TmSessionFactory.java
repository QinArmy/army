package io.army;

import io.army.sync.GenericSyncSessionFactory;

public interface TmSessionFactory extends GenericSyncSessionFactory, GenericTmSessionFactory {

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);

    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        TmSession build() throws SessionException;

    }
}
