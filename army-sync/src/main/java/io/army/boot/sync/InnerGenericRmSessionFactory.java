package io.army.boot.sync;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Dialect;
import io.army.sync.GenericSyncSessionFactory;

interface InnerGenericRmSessionFactory extends GenericRmSessionFactory, GenericSyncSessionFactory {

    Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

}
