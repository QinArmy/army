package io.army.criteria;

import io.army.dialect.SQLDialect;
import org.slf4j.Logger;

/**
 * @see Select
 * created  on 2018/10/21.
 */
public interface SQLDebug {

    default String debugSQL() {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(SQLDialect sqlDialect) {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(SQLDialect sqlDialect, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(Logger logger) {
        throw new UnsupportedOperationException();
    }

    default void debugSQL(SQLDialect sqlDialect, Logger logger) {
        throw new UnsupportedOperationException();
    }

    default void debugSQL(SQLDialect sqlDialect, Visible visible, Logger logger) {
        throw new UnsupportedOperationException();
    }
}
