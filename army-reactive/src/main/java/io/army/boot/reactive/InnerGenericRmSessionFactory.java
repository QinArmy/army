package io.army.boot.reactive;

import io.army.dialect.Dialect;
import io.army.reactive.GenericReactiveSessionFactory;
import io.army.session.GenericRmSessionFactory;

interface InnerGenericRmSessionFactory extends GenericReactiveSessionFactory, GenericRmSessionFactory {

    Dialect dialect();

    SelectSQLExecutor selectSQLExecutor();

    InsertSQLExecutor insertSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

}
