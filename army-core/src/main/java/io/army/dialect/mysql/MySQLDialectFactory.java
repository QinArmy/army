package io.army.dialect.mysql;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.util.Assert;

public abstract class MySQLDialectFactory {

    public static Dialect createMySQLDialect(Database sqlDialect, GenericRmSessionFactory sessionFactory) {
        Assert.notNull(sqlDialect, "dialect required");

        Dialect dialect;
        switch (sqlDialect) {
            case MySQL:
            case MySQL57:
                dialect = new MySQL57Dialect(sessionFactory);
                break;
            case MySQL80:
                dialect = new MySQL80Dialect(sessionFactory);
                break;
            default:
                throw new IllegalArgumentException("dialect not MySQL.");
        }
        return dialect;
    }
}
