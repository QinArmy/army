package io.army.sqltype;

import io.army.dialect.Database;
import io.army.dialect.SqlBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;

public interface SqlDataType {

    Database database();

    /**
     * @see Enum#name()
     */
    String name();


    @Deprecated
    default void zeroValue(FieldMeta<?, ?> fieldMeta, SqlBuilder builder, Database database) throws MetaException {

    }

    @Deprecated
    default void dataTypeClause(FieldMeta<?, ?> fieldMeta, SqlBuilder builder) throws MetaException {
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
    default void nowValue(FieldMeta<?, ?> fieldMeta, SqlBuilder builder, Database database)
            throws MetaException {
        throw new MetaException("%s,SQLDataType[%s] not support IDomain.NOW.", fieldMeta, name());
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
