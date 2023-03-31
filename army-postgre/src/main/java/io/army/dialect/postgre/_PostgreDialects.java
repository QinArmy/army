package io.army.dialect.postgre;

import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;
import io.army.dialect.DialectParserFactory;

public abstract class _PostgreDialects extends DialectParserFactory {

    private _PostgreDialects() {
        throw new UnsupportedOperationException();
    }


    public DialectParser create(final DialectEnv env) {
        return PostgreDialectParser.create(env, (PostgreDialect) targetDialect(env, Database.PostgreSQL));
    }


}
