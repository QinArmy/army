package io.army.sqltype;

import io.army.criteria.MetaException;
import io.army.dialect.Database;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;

public interface SQLDataType {

    Database database();

    String name();

    void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) throws MetaException;

    default void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
        builder.append(typeName());
    }

    default String typeName() {
        return name();
    }

    default void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
            throws MetaException {
        throw new MetaException("%s,SQLDataType[%s] not support io.army.domain.IDomain.NOW.", fieldMeta, name());
    }

    default boolean supportZeroValue(Database database) {
        return true;
    }

    default boolean supportNowValue(Database database) {
        return false;
    }
}
