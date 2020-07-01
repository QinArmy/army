package io.army.boot;

import io.army.GenericSyncSessionFactory;
import io.army.dialect.SQLDialect;

public interface RmSessionFactory extends GenericSyncSessionFactory {

    boolean supportZone();

    SQLDialect actualSQLDialect();

}
