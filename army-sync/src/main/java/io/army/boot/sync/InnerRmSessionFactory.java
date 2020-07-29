package io.army.boot.sync;

import io.army.GenericRmSessionFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;

interface InnerRmSessionFactory extends GenericRmSessionFactory {

    Dialect dialect();

    InsertSQLExecutor insertSQLExecutor();

    SelectSQLExecutor selectSQLExecutor();

    UpdateSQLExecutor updateSQLExecutor();

    CurrentSessionContext currentSessionContext();

}
