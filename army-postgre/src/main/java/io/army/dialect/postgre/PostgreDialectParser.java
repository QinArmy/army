package io.army.dialect.postgre;

import io.army.dialect.DialectEnv;
import io.army.dialect.DialectParser;


final class PostgreDialectParser extends PostgreParser {

    static DialectParser create(DialectEnv environment, PostgreDialect dialect) {
        return new PostgreDialectParser(environment, dialect);
    }

    private PostgreDialectParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
    }


}
