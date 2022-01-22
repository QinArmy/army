package io.army.dialect;

import io.army.Database;
import io.army.dialect.mysql._MySQLDialectFactory;
import io.army.util._Exceptions;

public abstract class _DialectFactory {

    private _DialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static _Dialect createDialect(_DialectEnvironment environment) {
        final Database database = environment.serverMeta().database();
        final _Dialect dialect;
        switch (database) {
            case MySQL:
                dialect = _MySQLDialectFactory.createDialect(environment);
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
