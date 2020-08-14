package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.SQLDataType;

import java.util.Map;

public abstract class AbstractMappingType implements MappingMeta {

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }


    @Override
    public final MappingMeta mappingMeta() {
        return this;
    }

    @Override
    public final String toConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
        if (paramMeta != null && paramMeta.mappingMeta() != this) {
            throw new IllegalArgumentException(String.format("paramMeta[%s] and %s not match."
                    , paramMeta.getClass().getName(), this.getClass().getName()));
        }
        if (!javaType().isInstance(nonNullValue)) {
            throw new IllegalArgumentException(String.format("value class[%s] and %s not match."
                    , nonNullValue.getClass().getName(), this.getClass().getName()));
        }
        return doToConstant(paramMeta, nonNullValue);
    }


    @Override
    public SQLDataType sqlDataType(Database database) throws NotSupportDialectException {
        SQLDataType dataType = sqlDataTypeMap().get(database.family());
        if (dataType == null) {
            throw MappingMetaUtils.createNotSupportDialectException(this, database);
        }
        return dataType;
    }


    @Override
    public final String toString() {
        return javaType().getName() + "#" + jdbcType().name() + "#";
    }


    protected abstract Map<Database, SQLDataType> sqlDataTypeMap();

    protected abstract String doToConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue);


}
