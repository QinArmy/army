package io.army.sync;

import io.army.dialect.Dialect;
import io.army.session.DialectSessionFactory;

interface InnerGenericRmSessionFactory extends DialectSessionFactory, GenericSyncSessionFactory {

    Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

    boolean springApplication();

}
