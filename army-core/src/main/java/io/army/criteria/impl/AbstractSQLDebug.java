package io.army.criteria.impl;

import io.army.criteria.SQLDebug;
import io.army.criteria.Statement;
import io.army.criteria.Visible;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.SchemaMeta;
import io.army.session.GenericSessionFactory;
import io.army.stmt.SimpleStmt;

import java.util.Collections;
import java.util.List;

abstract class AbstractSQLDebug implements Statement.SQLAble, SQLDebug {

    @Override
    public final String debugSQL() {
        return debugSQL(Database.MySQL, Visible.ONLY_VISIBLE);
    }

    @Override
    public final String debugSQL(Database sqlDialect) {
        return debugSQL(sqlDialect, Visible.ONLY_VISIBLE);
    }




    /*################################## blow protected template method ##################################*/


    GenericSessionFactory createSessionFactory(SchemaMeta schemaMeta, Database sqlDialect) {
        return null;
    }

    String printSQL(List<SimpleStmt> sqlWrapperList, Dialect dialect) {
        StringBuilder builder = new StringBuilder();
        for (SimpleStmt wrapper : sqlWrapperList) {
            builder.append(wrapper)
                    .append("\n")
            ;
        }
        return builder.toString();
    }


    protected static <E> List<E> asUnmodifiableList(@Nullable List<E> nullableList) {
        return nullableList == null ? Collections.emptyList() : Collections.unmodifiableList(nullableList);
    }
}
