package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.session.GenericRmSessionFactory;
import io.army.util.Assert;

public abstract class MySQLDialectFactory {

    public static Dialect createMySQLDialect(Database database, GenericRmSessionFactory sessionFactory) {
        Assert.notNull(database, "dialect required");

        Dialect dialect;
        switch (database) {
            case MySQL:
           // case MySQL57:
                dialect = new MySQL57Dialect(sessionFactory);
                break;
            default:
                throw new IllegalArgumentException("dialect not MySQL.");
        }
        return dialect;
    }
}
