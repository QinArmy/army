package io.army.boot;

import io.army.SessionFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;

interface InnerSyncSessionFactory extends SessionFactory, InnerGenericSessionFaction {

    Dialect dialect();

    CurrentSessionContext currentSessionContext();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();
}
