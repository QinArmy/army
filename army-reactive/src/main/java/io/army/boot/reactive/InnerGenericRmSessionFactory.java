package io.army.boot.reactive;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Dialect;
import io.army.reactive.GenericReactiveSessionFactory;

interface InnerGenericRmSessionFactory extends GenericReactiveSessionFactory, GenericRmSessionFactory {

    Dialect dialect();

    SelectSQLExecutor selectSQLExecutor();

    InsertSQLExecutor insertSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

}
