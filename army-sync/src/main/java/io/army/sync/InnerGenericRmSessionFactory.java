package io.army.sync;

import io.army.dialect.Dialect;
import io.army.session.GenericRmSessionFactory;

interface InnerGenericRmSessionFactory extends GenericRmSessionFactory, GenericSyncSessionFactory {

    Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

    boolean springApplication();

}
