package io.army;

import io.army.dialect.SQLDialect;
import io.army.meta.TableMeta;
import io.army.sharding.DatabaseRoute;
import io.army.sync.GenericSyncSessionFactory;

import java.util.Map;

public interface TmSessionFactory extends GenericSyncSessionFactory {

    boolean supportZone();

    Map<String, SQLDialect> actualSQLDialectMap();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);

    DatabaseRoute dataSourceRoute(TableMeta<?> tableMeta);

    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        TmSession build() throws SessionException;

    }
}
