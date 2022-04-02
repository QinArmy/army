package io.army.dialect.mysql;

import io.army.dialect.Dialect;
import io.army.dialect._DialectEnvironment;

final class MySQL57 extends MySQLDialect {

    static MySQL57 create(_DialectEnvironment environment, Dialect dialect) {
        return new MySQL57(environment, dialect);
    }

    private MySQL57(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
    }


}
