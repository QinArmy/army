package io.army.criteria;

/**
 * <p>This interface is base interface of {@link io.army.sqltype.DataType} :
 *
 * @since 1.0
 */
public interface TypeDef extends TypeItem {

    /**
     * <p>SQL type's type name in database.
     *
     * @return SQL type's name in database
     */
    String typeName();


}
