package io.army.sqltype;

/**
 * <p>This is base interface of following:
 * <ul>
 *     <li>{@link SqlType}</li>
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
     * <p>SQL type's type name in database.
     *
     * @return SQL type's name in database
     */
    String typeName();

    boolean isUnknown();

    boolean isArray();


    /**
     * <p>This method is equivalent to {@code   DataType.from(typeName,false)} :
     * <p><strong>NOTE</strong>: only when {@link ArmyType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link SqlType}.
     *
     * @param typeName non-null
     * @return {@link DataType} instance
     * @see #from(String, boolean)
     */
    static DataType from(String typeName) {
        return DataTypeFactory.typeFrom(typeName, false);
    }

    /**
     * <p>Get one {@link DataType} instance
     * <p><strong>NOTE</strong>: only when {@link ArmyType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link SqlType}.
     *
     * @param typeName        database data type name,if typeName endWith '[]',then {@link DataType#isArray()} always return true.
     * @param caseSensitivity if false ,then {@link DataType#typeName()} always return upper case.
     * @return {@link DataType} that representing user-defined type.
     * @throws IllegalArgumentException throw when typeName have no text.
     */
    static DataType from(String typeName, boolean caseSensitivity) {
        return DataTypeFactory.typeFrom(typeName, caseSensitivity);
    }

}
