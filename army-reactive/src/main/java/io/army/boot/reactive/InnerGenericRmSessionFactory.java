package io.army.boot.reactive;

import io.army.dialect.Dialect;

interface InnerGenericRmSessionFactory extends GenericReactiveRmSessionFactory {

     Dialect dialect();

     ReactiveSelectSQLExecutor selectSQLExecutor();

     ReactiveInsertSQLExecutor insertSQLExecutor();

     ReactiveUpdateSQLExecutor updateSQLExecutor();
}
