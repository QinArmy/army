package io.army.dialect.postgre;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.session.GenericRmSessionFactory;
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
                dialect = new Postgre11Dialect(sessionFactory);
                break;
            default:
                throw new IllegalArgumentException(String.format("database[%s] not Postgre.", database));
        }
        return dialect;
    }
}
