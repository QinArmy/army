package io.army.dialect.mysql;

import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;
import io.army.dialect.DialectParserFactory;
import io.army.util.ClassUtils;

public abstract class _MySQLDialectFactory extends DialectParserFactory {

    private _MySQLDialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static DialectParser mysqlDialectParser(final DialectEnv environment) {
        final String className = "io.army.dialect.mysql._MySQLDialects";
        final MySQLParser _dialect;
        if (ClassUtils.isPresent(className, MySQLParser.class.getClassLoader())) {
            _dialect = DialectParserFactory.invokeFactoryMethod(MySQLParser.class, className, environment);
        } else {
            _dialect = MySQLParser.standard(environment, (MySQLDialect) targetDialect(environment, Database.MySQL));
        }
        return _dialect;
    }


}
