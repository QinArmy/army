package io.army.boot.reactive;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Dialect;

interface InnerGenericRmSessionFactory extends GenericRmSessionFactory {

     Dialect dialect();

     ReactiveSelectSQLExecutor selectSQLExecutor();

     ReactiveInsertSQLExecutor insertSQLExecutor();

     ReactiveUpdateSQLExecutor updateSQLExecutor();
}
