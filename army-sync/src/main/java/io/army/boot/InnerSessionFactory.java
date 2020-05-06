package io.army.boot;

import io.army.SessionFactory;
import io.army.context.spi.CurrentSessionContext;

import javax.sql.DataSource;

interface InnerSessionFactory extends SessionFactory, InnerGenericSessionFaction {

    DataSource dataSource();

    CurrentSessionContext currentSessionContext();

    InsertSQLExecutor insertSQLExecutor();
}
