package io.army.dialect.mysql;

import io.army.dialect.Dialect;
import io.army.dialect._DialectEnvironment;
import io.army.dialect._DialectFactory;
import io.army.session.Database;
import io.army.util._Exceptions;

@SuppressWarnings("unused")
public abstract class _MySQLDialects extends _DialectFactory {

    private _MySQLDialects() {
        throw new UnsupportedOperationException();
    }

    public MySQL create(final _DialectEnvironment environment) {
        final Dialect targetDialect;
        targetDialect = targetDialect(environment, Database.MySQL);
        final MySQL mySQL;
        switch (targetDialect) {
            case MySQL56:
            case MySQL57:
                mySQL = MySQL57.create(environment, targetDialect);
                break;
            case MySQL80:
                mySQL = MySQL80.create(environment, targetDialect);
                break;
            default:
                throw _Exceptions.unexpectedEnum(targetDialect);
        }
        return mySQL;
    }


}
