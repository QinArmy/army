package io.army.sync;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Dialect;

interface InnerGenericRmSessionFactory extends GenericRmSessionFactory, GenericSyncSessionFactory {

    Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

    boolean springApplication();

}
