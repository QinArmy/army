package io.army.criteria.impl;

import io.army.dialect.Database;
import io.army.dialect.Dialect;


/**
 * package enum,This enum is designed for standard statement. For example : {@link StandardQueries}
 *
 * @since 0.6.0
 */
enum StandardDialect implements Dialect {

    STANDARD10(10),
    STANDARD20(20);

    private final byte version;

    StandardDialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        //no bug,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public final int compareWith(final Dialect o) {
        if (!(o instanceof StandardDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((StandardDialect) o).version;
    }

    @Override
    public final boolean isFamily(final Dialect o) {
        return o instanceof StandardDialect;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
