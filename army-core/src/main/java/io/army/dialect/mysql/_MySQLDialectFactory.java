package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;
import io.army.dialect._DialectFactory;
import io.army.util._ClassUtils;

public abstract class _MySQLDialectFactory extends _DialectFactory {

    private _MySQLDialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static DialectParser createDialect(final DialectEnv environment) {
        final String className = "io.army.dialect.mysql._MySQLDialects";
        final MySQLParser _dialect;
        if (_ClassUtils.isPresent(className, MySQLParser.class.getClassLoader())) {
            _dialect = _DialectFactory.invokeFactoryMethod(MySQLParser.class, className, environment);
        } else {
            _dialect = MySQLParser.standard(environment, (MySQLDialect) targetDialect(environment, Database.MySQL));
        }
        return _dialect;
    }


}
