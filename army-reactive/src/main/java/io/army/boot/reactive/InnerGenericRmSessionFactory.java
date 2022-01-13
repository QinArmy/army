package io.army.boot.reactive;

import io.army.dialect.Dialect;
import io.army.reactive.GenericReactiveSessionFactory;
import io.army.session.DialectSessionFactory;

interface InnerGenericRmSessionFactory extends GenericReactiveSessionFactory, DialectSessionFactory {

    Dialect dialect();

    SelectSQLExecutor selectSQLExecutor();

    InsertSQLExecutor insertSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

}
