package io.army.dialect.mysql;

import io.army.dialect._Dialect;
import io.army.dialect._DialectEnvironment;
import io.army.dialect._DialectFactory;
import io.army.session.Database;
import io.army.util._ClassUtils;

public abstract class _MySQLDialectFactory extends _DialectFactory {

    private _MySQLDialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static _Dialect createDialect(final _DialectEnvironment environment) {
        final String className = "io.army.dialect.mysql._MySQLDialects";
        final MySQL _dialect;
        if (_ClassUtils.isPresent(className, MySQL.class.getClassLoader())) {
            _dialect = _DialectFactory.create(MySQL.class, className, environment);
        } else {
            _dialect = new MySQL(environment, targetDialect(environment, Database.MySQL));
        }
        return _dialect;
    }


}
