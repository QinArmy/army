package io.army.sync;

import io.army.dialect._Dialect;
import io.army.session.DialectSessionFactory;

interface InnerGenericRmSessionFactory extends DialectSessionFactory, GenericSyncSessionFactory {

    _Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

    boolean springApplication();

}
