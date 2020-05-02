package io.army.criteria.impl;

import io.army.GenericSessionFactory;
import io.army.criteria.SQLAble;
import io.army.criteria.SQLDebug;
import io.army.criteria.Visible;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.meta.SchemaMeta;
import io.army.wrapper.SQLWrapper;

import java.util.List;

abstract class AbstractSQLDebug implements SQLAble, SQLDebug {

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

    String printSQL(List<SQLWrapper> sqlWrapperList, Dialect dialect) {
        StringBuilder builder = new StringBuilder();
        for (SQLWrapper wrapper : sqlWrapperList) {
            builder.append(wrapper)
                    .append("\n")
            ;
        }
        return builder.toString();
    }
}
