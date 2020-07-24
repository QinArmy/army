package io.army.criteria.impl;

import io.army.GenericSessionFactory;
import io.army.criteria.SQLDebug;
import io.army.criteria.SQLStatement;
import io.army.criteria.Visible;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.meta.SchemaMeta;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

abstract class AbstractSQLDebug implements SQLStatement.SQLAble, SQLDebug {

    @Override
    public final String debugSQL() {
        return debugSQL(SQLDialect.MySQL57, Visible.ONLY_VISIBLE);
    }

    @Override
    public final String debugSQL(SQLDialect sqlDialect) {
        return debugSQL(sqlDialect, Visible.ONLY_VISIBLE);
    }


    /*################################## blow protected template method ##################################*/


    GenericSessionFactory createSessionFactory(SchemaMeta schemaMeta, SQLDialect sqlDialect) {
        return null;
    }

    String printSQL(List<SimpleSQLWrapper> sqlWrapperList, Dialect dialect) {
        StringBuilder builder = new StringBuilder();
        for (SimpleSQLWrapper wrapper : sqlWrapperList) {
            builder.append(wrapper)
                    .append("\n")
            ;
        }
        return builder.toString();
    }
}
