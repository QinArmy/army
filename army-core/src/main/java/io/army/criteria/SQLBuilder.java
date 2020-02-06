package io.army.criteria;

import io.army.dialect.SQLDialect;

/**
 * created  on 2018/10/21.
 */
public interface SQLBuilder {

    String debugSQL();

    default String debugSQL(SQLDialect sqlDialect) {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(SQLDialect sqlDialect,Visible visible) {
        throw new UnsupportedOperationException();
    }



}
