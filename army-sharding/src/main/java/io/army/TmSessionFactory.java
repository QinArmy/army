package io.army;

import io.army.dialect.SQLDialect;
import io.army.meta.TableMeta;
import io.army.sharding.DataSourceRoute;
import io.army.sync.GenericSyncSessionFactory;

import java.util.Map;

public interface TmSessionFactory extends GenericSyncSessionFactory {

    boolean supportZone();

    Map<String, SQLDialect> actualSQLDialectMap();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);

    DataSourceRoute dataSourceRoute(TableMeta<?> tableMeta);

    SessionBuilder builder();

    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        TmSession build() throws SessionException;

    }
}
