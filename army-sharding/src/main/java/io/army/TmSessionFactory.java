package io.army;

import io.army.dialect.SQLDialect;

import java.util.Map;

public interface TmSessionFactory extends GenericSyncSessionFactory {

    boolean supportZone();

    Map<String, SQLDialect> actualSQLDialectMap();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);

    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        TmSession build() throws SessionException;

    }
}
