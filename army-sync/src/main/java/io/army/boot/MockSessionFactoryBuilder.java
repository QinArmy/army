package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.SessionFactory;
import io.army.SessionFactoryException;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.dialect.mysql.MySQLDialectFactory;

final class MockSessionFactoryBuilder extends AbstractSessionFactoryBuilder {



    @Override
    public SessionFactory build() throws SessionFactoryException {
        throw new UnsupportedOperationException();
    }

    private static Dialect createDialect(GenericSessionFactory sessionFactory, SQLDialect sqlDialect) {
        Dialect dialect;
        switch (sqlDialect) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                dialect = MySQLDialectFactory.createMySQLDialect(sqlDialect, sessionFactory);
                break;
            case SQL_Server:
            case OceanBase:
            case Postgre:
            case Oracle:
            case Db2:
            default:
                throw new IllegalArgumentException();
        }
        return dialect;
    }
}
