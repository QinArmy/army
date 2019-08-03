package io.army.meta;

import io.army.annotation.Column;
import io.army.criteria.dialect.Dialect;
import io.army.util.Precision;

import java.util.List;

/**
 * A common interface to all dialect-specific data types.
 */
public interface SQLType {

    /**
     * return sql date type name (e.g {@code VARCHAR} , {@code INT})
     *
     * @return sql date type name
     */
    String typeName();

    /**
     * return sql date type name (e.g {@code VARCHAR(64)} , {@code INT(11)}) with precision
     *
     * @return sql date type name with precision
     */
    String typeName(int precision);

    /**
     * return sql date type name (e.g {@code DECIMAL(14,2)} {@code DECIMAL(9,6)}) with precision
     *
     * @return sql date type name with precision and scale
     */
    default String typeName(int precision, int scale) {
        return this.typeName(precision);
    }

    /**
     * return sql data type default precision and scale,the precision and scale will be use creation statement.
     * <p>
     *     if return {@link Precision#EMPTY} ,{@link Column#length()} will be ignored .
     * </p>
     * @return default precision and scale or {@link Precision#EMPTY}
     */
    Precision defaultPrecision();

    /**
     * @return dialect list that this sql date type is supported
     */
    List<Dialect> dialectList();

}
