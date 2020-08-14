package io.army.criteria;

import io.army.dialect.Database;
import org.slf4j.Logger;

/**
 * @see Select
 * @since 1.0
 */
public interface SQLDebug {

    default String debugSQL() {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(Database sqlDialect) {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(Database sqlDialect, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default String debugSQL(Logger logger) {
        throw new UnsupportedOperationException();
    }

    default void debugSQL(Database sqlDialect, Logger logger) {
        throw new UnsupportedOperationException();
    }

    default void debugSQL(Database sqlDialect, Visible visible, Logger logger) {
        throw new UnsupportedOperationException();
    }
}
