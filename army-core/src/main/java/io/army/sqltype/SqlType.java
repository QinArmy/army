package io.army.sqltype;

import io.army.Database;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;

public interface SqlType {

    Database database();

    /**
     * @see Enum#name()
     */
    String name();


    @Deprecated
    default void zeroValue(FieldMeta<?, ?> fieldMeta, StringBuilder builder, Database database) throws MetaException {

    }

    @Deprecated
    default void dataTypeClause(FieldMeta<?, ?> fieldMeta, StringBuilder builder) throws MetaException {
        builder.append(typeName());
    }


    /**
     * sql data type name,must uppercase.
     */
    @Deprecated
    default String typeName() {
        return name();
    }

    @Deprecated
    default void nowValue(FieldMeta<?, ?> fieldMeta, StringBuilder builder, Database database)
            throws MetaException {
        String m = String.format("%s,SQLDataType[%s] not support IDomain.NOW.", fieldMeta, name());
        throw new MetaException(m);
    }

    @Deprecated
    default boolean supportZeroValue(Database database) {
        return true;
    }

    @Deprecated
    default boolean supportNowValue(Database database) {
        return false;
    }


}
