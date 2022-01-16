package io.army.dialect.postgre;

import io.army.dialect.Database;
import io.army.dialect._Dialect;
import io.army.session.DialectSessionFactory;

public abstract class PostgreDialectFactory {

    private PostgreDialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static _Dialect createPostgreDialect(Database database, DialectSessionFactory sessionFactory) {
//        _Assert.notNull(database, "dialect required");
//
//        Dialect dialect;
//        switch (database) {
//            case PostgreSQL:
//                dialect = new Postgre11Dialect(sessionFactory);
//                break;
//            default:
//                throw new IllegalArgumentException(String.format("database[%s] not Postgre.", database));
//        }
        return null;
    }
}
