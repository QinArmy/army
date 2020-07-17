package io.army.boot;

import io.army.SessionFactory;
import io.army.dialect.Dialect;

interface InnerSyncSessionFactory extends SessionFactory, InnerGenericSessionFaction {

    Dialect dialect();


    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();
}
