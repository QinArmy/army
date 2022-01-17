package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.DialectEnvironment;
import io.army.dialect._Dialect;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;

public abstract class MySQLDialectFactory {

    public static _Dialect createDialect(final DialectEnvironment environment) {
        final ServerMeta meta = environment.serverMeta();
        if (meta.database() != Database.MySQL) {
            throw new IllegalArgumentException();
        }
        final _Dialect dialect;
        switch (meta.major()) {
            case 5: {
                if (meta.minor() >= 6) {
                    dialect = MySQL57.create(environment);
                } else {
                    throw _Exceptions.notServerVersion(meta);
                }
            }
            break;
            case 8:
                dialect = MySQL80Dialect.create(environment);
                break;
            default:
                throw _Exceptions.notServerVersion(meta);
        }
        return dialect;
    }


}
