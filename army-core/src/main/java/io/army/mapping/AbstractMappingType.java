package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.ParamException;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;

public abstract class AbstractMappingType implements MappingType {

    protected AbstractMappingType() {
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final MappingType mappingType() {
        return this;
    }


    @Override
    public final String toString() {
        return String.format("%s javaType[%s] jdbcType[%s]", super.toString(), javaType().getName(), jdbcType());
    }

    protected final ParamException notSupportConvertBeforeBind(final Object nonNull) {
        String m = String.format("Not support convert %s for %s bind.", nonNull, javaType().getName());
        return new ParamException(m);
    }

    @Deprecated
    protected final ParamException notSupportConvertAfterGet(final Object nonNull) {
        String m = String.format("Not support convert from %s to %s.", nonNull, javaType().getName());
        return new ParamException(m);
    }


    @Deprecated
    protected final ParamException outRangeOfType(final Object nonNull, @Nullable final Throwable cause) {
        String m = String.format("Parameter[%s] value[%s] out of range %s"
                , nonNull.getClass().getName(), nonNull, this);
        return cause == null ? new ParamException(m) : new ParamException(m, cause);
    }

    protected static DataAccessException errorJavaTypeForSqlType(SqlType sqlType, final Object nonNull) {
        String m = String.format("Statement executor passing error java type[%s] for %s.%s ."
                , nonNull.getClass().getName(), sqlType.getClass().getSimpleName(), sqlType.name());
        return new DataAccessException(m);
    }


    protected static DataAccessException errorValueForSqlType(SqlType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        final String m = String.format("Statement executor passing error java type[%s] value for %s.%s ."
                , nonNull.getClass().getName(), sqlType.getClass().getSimpleName(), sqlType.name());
        final DataAccessException exception;
        if (cause == null) {
            exception = new DataAccessException(m);
        } else {
            exception = new DataAccessException(m, cause);
        }
        return exception;
    }

    protected static CriteriaException valueOutRange(SqlType sqlType, final Object nonNull, @Nullable Throwable cause) {
        return _Exceptions.valueOutRange(sqlType, nonNull, cause);
    }

    protected static CriteriaException outRangeOfSqlType(SqlType sqlType, final Object nonNull) {
        return _Exceptions.outRangeOfSqlType(sqlType, nonNull);
    }

    protected static CriteriaException outRangeOfSqlType(SqlType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        return _Exceptions.outRangeOfSqlType(sqlType, nonNull, cause);
    }


    protected final NotSupportDialectException noMappingError(ServerMeta serverMeta) {
        String m = String.format("No mapping from java type[%s] to Server[%s]", javaType(), serverMeta);
        return new NotSupportDialectException(m);
    }

    protected static IllegalArgumentException errorJavaType(
            Class<? extends MappingType> mappingMetaClass, Class<?> javaType) {
        return new IllegalArgumentException(
                String.format("%s not support java type[%s].", mappingMetaClass.getName(), javaType.getName()));
    }


}
