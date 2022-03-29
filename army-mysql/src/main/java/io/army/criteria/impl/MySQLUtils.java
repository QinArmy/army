package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.dialect.Dialect;
import io.army.session.Database;
import io.army.util._Exceptions;

abstract class MySQLUtils {

    private MySQLUtils() {
        throw new UnsupportedOperationException();
    }

    static Dialect defaultDialect(Statement statement) {
        return statement instanceof _MySQLWithClause ? Dialect.MySQL80 : Dialect.MySQL57;
    }

    static void validateDialect(Statement statement, Dialect dialect) {
        if (dialect.database() != Database.MySQL) {
            throw _Exceptions.stmtDontSupportDialect(dialect);
        }
        if (statement instanceof _MySQLWithClause && dialect.version() < Dialect.MySQL80.version()) {
            throw _Exceptions.stmtDontSupportDialect(dialect);
        }
    }


}
