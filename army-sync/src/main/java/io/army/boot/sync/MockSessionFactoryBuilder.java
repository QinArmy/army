package io.army.boot.sync;

import io.army.GenericSessionFactory;
import io.army.SessionFactoryException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.sync.SessionFactory;

final class MockSessionFactoryBuilder extends AbstractSyncSessionFactoryBuilder {


    @Override
    public SessionFactory build() throws SessionFactoryException {
        throw new UnsupportedOperationException();
    }

    private static Dialect createDialect(GenericSessionFactory sessionFactory, Database sqlDialect) {
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
