package io.army.dialect.postgre;

import io.army.dialect.DialectEnv;


final class PostgreDialectParser extends PostgreParser {

    static PostgreDialectParser create(DialectEnv environment, PostgreDialect dialect) {
        return new PostgreDialectParser(environment, dialect);
    }

    private PostgreDialectParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
    }


}
