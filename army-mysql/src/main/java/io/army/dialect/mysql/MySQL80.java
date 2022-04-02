package io.army.dialect.mysql;

import io.army.dialect.Dialect;
import io.army.dialect._DialectEnvironment;

final class MySQL80 extends MySQLDialect {

    static MySQL80 create(_DialectEnvironment environment, Dialect dialect) {
        return new MySQL80(environment, dialect);
    }

    private MySQL80(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
    }

}
