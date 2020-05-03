package io.army.boot;

import io.army.SessionFactory;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;

import javax.sql.DataSource;

interface InnerSessionFactory extends SessionFactory {

    DataSource dataSource();

    Dialect dialect();

    CurrentSessionContext currentSessionContext();

    InsertSQLExecutor insertSQLExecutor();
}
