package io.army.dialect.mysql;

import io.army.GenericSessionFactory;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.util.Assert;

public abstract class MySQLDialectFactory {

    public static Dialect createMySQLDialect(SQLDialect sqlDialect, GenericSessionFactory sessionFactory) {
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
