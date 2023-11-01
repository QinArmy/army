package io.army.session.record;

/**
 * <p>This is base interface of following:
 * <ul>
 *     <li>{@link io.army.sqltype.SQLType}</li>
 *     <li>{@link ArmyType}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface DataType {


    /**
     * <p>SQL type's alias (not type name) in java language.
     *
     * @see #typeName()
     * @see Enum#name()
     */
    String name();

    /**
     * <p>SQL type's alias (not type name) in database.
     *
     * @return SQL type's name in database
     */
    String typeName();

    boolean isUnknown();

    boolean isArray();


}
