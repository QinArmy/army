package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect._DialectEnv;
import io.army.dialect._DialectFactory;
import io.army.util._Exceptions;

@SuppressWarnings("unused")
public abstract class _MySQLDialects extends _DialectFactory {

    private _MySQLDialects() {
        throw new UnsupportedOperationException();
    }

    public static MySQLParser create(final _DialectEnv env) {
        final MySQLDialect targetDialect;
        targetDialect = (MySQLDialect) targetDialect(env, Database.MySQL);
        final MySQLParser mySQL;
        switch (targetDialect) {
            case MySQL55:
            case MySQL56:
            case MySQL57:
            case MySQL80:
                mySQL = MySQLDialectParser.create(env, targetDialect);
                break;
            default:
                throw _Exceptions.unexpectedEnum(targetDialect);
        }
        return mySQL;
    }


}
