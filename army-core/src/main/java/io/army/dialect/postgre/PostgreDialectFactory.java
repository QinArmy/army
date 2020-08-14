package io.army.dialect.postgre;

import io.army.GenericRmSessionFactory;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.util.Assert;

public abstract class PostgreDialectFactory {

    private PostgreDialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static Dialect createPostgreDialect(Database database, GenericRmSessionFactory sessionFactory) {
        Assert.notNull(database, "dialect required");

        Dialect dialect;
        switch (database) {
            case Postgre:
            case Postgre11:
                dialect = new Postgre11Dialect(sessionFactory);
                break;
            case Postgre12:
            default:
                throw new IllegalArgumentException(String.format("database[%s] not Postgre.", database));
        }
        return dialect;
    }
}
