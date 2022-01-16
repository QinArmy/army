package io.army.dialect;

import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.util._Exceptions;

public abstract class DialectFactory {

    private DialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static _Dialect createDialect(DialectEnvironment environment) {
        final Database database = environment.serverMeta().database();
        final _Dialect dialect;
        switch (database) {
            case MySQL:
                dialect = MySQLDialectFactory.createDialect(environment);
                break;
            case PostgreSQL:
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return dialect;
    }


}
