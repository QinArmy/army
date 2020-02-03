package io.army.criteria;

import io.army.dialect.SQLDialect;

/**
 * created  on 2018/10/21.
 */
public interface SQLBuilder {


    default String build() {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(SQLDialect sqlDialect) {
        throw new UnsupportedOperationException();
    }


    default String buildWithParam() {
        throw new UnsupportedOperationException();
    }


}
