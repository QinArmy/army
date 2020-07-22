package io.army.boot;

import io.army.dialect.SQLDialect;
import io.army.sync.GenericSyncSessionFactory;

public interface RmSessionFactory extends GenericSyncSessionFactory {

    boolean supportZone();

    SQLDialect actualSQLDialect();

}
