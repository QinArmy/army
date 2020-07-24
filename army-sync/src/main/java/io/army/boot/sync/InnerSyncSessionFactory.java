package io.army.boot.sync;

import io.army.boot.InnerGenericSessionFaction;
import io.army.dialect.Dialect;
import io.army.sync.SessionFactory;

interface InnerSyncSessionFactory extends SessionFactory, InnerGenericSessionFaction {

    Dialect dialect();


    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();
}
