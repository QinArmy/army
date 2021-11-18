package io.army.dialect;

import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.session.GenericRmSessionFactory;
import io.army.util.Exceptions;

public abstract class DialectFactory {

    private DialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static Dialect createDialect(GenericRmSessionFactory factory) {
        final Database database = factory.serverMeta().database();
        final Dialect dialect;
        switch (database) {
            case MySQL:
                dialect = MySQLDialectFactory.createDialect(factory);
                break;
            case PostgreSQL:
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw Exceptions.createUnexpectedEnumException(database);
        }
        return dialect;
    }


}
