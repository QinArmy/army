package io.army.criteria.impl;

import io.army.SessionFactory;
import io.army.boot.SessionFactoryBuilder;
import io.army.criteria.SQLAble;
import io.army.criteria.SQLBuilder;
import io.army.criteria.Visible;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.dialect.SQLWrapper;
import io.army.meta.SchemaMeta;

import java.util.List;

abstract class AbstractSQLAble implements SQLAble, SQLBuilder {

    @Override
    public final String debugSQL() {
        return debugSQL(SQLDialect.MySQL57, Visible.ONLY_VISIBLE);
    }

    @Override
    public final String debugSQL(SQLDialect sqlDialect) {
        return debugSQL(sqlDialect, Visible.ONLY_VISIBLE);
    }


    /*################################## blow protected template method ##################################*/


    SessionFactory createSessionFactory(SchemaMeta schemaMeta, SQLDialect sqlDialect) {
        return SessionFactoryBuilder.mockBuilder()
                .catalog(schemaMeta.catalog())
                .schema(schemaMeta.schema())
                .sqlDialect(sqlDialect)
                .build();
    }

    String printSQL(List<SQLWrapper> sqlWrapperList, Dialect dialect) {
        StringBuilder builder = new StringBuilder();
        for (SQLWrapper wrapper : sqlWrapperList) {
            builder.append(builder)
                    .append("\n")
            ;
        }
        return builder.toString();
    }
}
