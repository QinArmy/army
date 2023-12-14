package io.army.dialect.postgre;

import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;
import io.army.dialect.DialectParserFactory;
import io.army.util.ClassUtils;

public abstract class _PostgreDialectFactory extends DialectParserFactory {

    private _PostgreDialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static DialectParser postgreDialectParser(DialectEnv environment) {
        final String className = "io.army.dialect.postgre._PostgreDialects";
        final PostgreParser parser;
        if (ClassUtils.isPresent(className, PostgreParser.class.getClassLoader())) {
            parser = DialectParserFactory.invokeFactoryMethod(PostgreParser.class, className, environment);
        } else {
            parser = PostgreParser.standard(environment, (PostgreDialect) targetDialect(environment, Database.PostgreSQL));
        }
        return parser;
    }

}
