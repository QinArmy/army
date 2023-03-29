package io.army.mapping;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.session.DataAccessException;
import io.army.session.ParamException;
import io.army.sqltype.SqlType;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.time.temporal.Temporal;
import java.util.function.BiFunction;

public abstract class AbstractMappingType implements MappingType {

    protected static final BiFunction<MappingType, Object, ArmyException> PARAM_ERROR_HANDLER = AbstractMappingType::paramError;

    protected static final BiFunction<MappingType, Object, ArmyException> DATA_ACCESS_ERROR_HANDLER = AbstractMappingType::dataAccessError;

    protected static final BiFunction<MappingType, ServerMeta, NotSupportDialectException> MAP_ERROR_HANDLER = AbstractMappingType::mapError;
    ;


    protected AbstractMappingType() {
    }

    @Override
    public final MappingType mappingType() {
        return this;
    }

    @Override
    public final TypeMeta typeMeta() {
        return this;
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
    public final String toString() {
        return _StringUtils.builder()
                .append(this.getClass().getName())
                .append("[javaType:")
                .append(this.javaType().getName())
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
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


    @Deprecated
    protected final NotSupportDialectException noMappingError(ServerMeta serverMeta) {
        String m = String.format("No mapping from java type[%s] to Server[%s]", javaType(), serverMeta);
        return new NotSupportDialectException(m);
    }

    protected static IllegalArgumentException errorJavaType(
            Class<? extends MappingType> mappingMetaClass, Class<?> javaType) {
        return new IllegalArgumentException(
                String.format("%s not support java type[%s].", mappingMetaClass.getName(), javaType.getName()));
    }

    protected static IllegalArgumentException valueOutOfMapping(final Object nonNull
            , Class<? extends MappingType> typeClass) {
        String m = String.format("value[%s] out of range of %s .", nonNull, typeClass.getName());
        return new IllegalArgumentException(m);
    }


    private static CriteriaException paramError(final MappingType type, final Object nonNull) {
        return new CriteriaException(createConvertErrorMessage(type, nonNull));
    }

    private static DataAccessException dataAccessError(final MappingType type, final Object nonNull) {
        return new DataAccessException(createConvertErrorMessage(type, nonNull));
    }

    private static NotSupportDialectException mapError(final MappingType type, final ServerMeta meta) {
        String m = String.format("%s don't support %s", type, meta);
        return new NotSupportDialectException(m);
    }

    private static String createConvertErrorMessage(final MappingType type, final Object nonNull) {
        final StringBuilder builder = new StringBuilder();
        builder.append(_ClassUtils.safeClassName(nonNull));

        if (type instanceof _ArmyNoInjectionMapping
                || nonNull instanceof Number
                || nonNull instanceof Enum
                || nonNull instanceof Temporal) {
            builder.append('[')
                    .append(nonNull)
                    .append(']');
        }
        return builder.append(" couldn't be converted by ")
                .append(type)
                .toString();
    }


}
