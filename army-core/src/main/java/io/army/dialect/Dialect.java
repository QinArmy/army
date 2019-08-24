package io.army.dialect;

import io.army.meta.Field;

/**
 *  A common interface to all dialect of database.
 * created  on 2019-02-22.
 */
public interface Dialect {

    /**
     * return field 的 column definition clause.
     * <p>
     * e.g {@code id BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'primary key'}
     * </p>
     *
     * @param field column meta
     * @return column definition clause
     */
    String fieldDefinition(Field<?, ?> field);


}
