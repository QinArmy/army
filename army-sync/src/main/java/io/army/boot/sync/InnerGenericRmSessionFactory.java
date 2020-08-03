package io.army.boot.sync;

import io.army.GenericRmSessionFactory;
import io.army.boot.DomainValuesGenerator;
import io.army.dialect.Dialect;
import io.army.sync.GenericSyncSessionFactory;

interface InnerGenericRmSessionFactory extends GenericRmSessionFactory, GenericSyncSessionFactory {

    DomainValuesGenerator domainValuesGenerator();

    Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();



}
