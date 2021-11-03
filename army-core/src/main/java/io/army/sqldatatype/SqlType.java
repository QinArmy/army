package io.army.sqldatatype;

import io.army.dialect.Database;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;

public interface SqlType {

    Database database();

    /**
     * @see Enum#name()
     */
    String name();


    @Deprecated
    default void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) throws MetaException {

    }

    @Deprecated
    default void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
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
    default void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
            throws MetaException {
        throw new MetaException("%s,SQLDataType[%s] not support io.army.domain.IDomain.NOW.", fieldMeta, name());
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
