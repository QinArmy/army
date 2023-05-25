package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * <p>
 * This class representing Postgre int4range type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public final class PostgreInt4RangeType extends PostgreRangeType {

    public static PostgreInt4RangeType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        return TEXT;
    }

    public static <R> PostgreInt4RangeType func(final Class<? extends R> javaType,
                                                final _RangeFunction<Integer, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreInt4RangeType(javaType, function);
    }

    public static PostgreInt4RangeType method(final Class<?> javaType, final Method method) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        return new PostgreInt4RangeType(javaType, createRangeFunction(javaType, Integer.TYPE, method));
    }

    public static final PostgreInt4RangeType TEXT = new PostgreInt4RangeType(String.class, null);


    private final _RangeFunction<Integer, ?> function;

    /**
     * private constructor
     */
    private PostgreInt4RangeType(final Class<?> javaType, final @Nullable _RangeFunction<Integer, ?> function) {
        super(javaType);
        assert function != null || javaType == String.class;
        this.function = function;
    }


    @Override
    public MappingType arrayTypeOfThis() {
        final Class<?> javaType = this.javaType;
        final _RangeFunction<Integer, ?> function = this.function;
        final MappingType arrayType;
        if (javaType == String.class && function == null) {
            arrayType = PostgreInt4RangeArrayType.LINEAR;
        } else {
            assert function != null;
            arrayType = PostgreInt4RangeArrayType.func(_ArrayUtils.arrayClassOf(javaType), function);
        }
        return arrayType;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4RANGE;
    }

    @Override
    public Object convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        final _RangeFunction<Integer, ?> function = this.function;
        final Object value;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull);
            }
            value = nonNull;
        } else if (function == null) {
            assert this.javaType == String.class;
            value = nonNull;
        } else if (EMPTY.equals(nonNull)) {
            value = emptyRange(this.javaType);
        } else {
            value = textToRange((String) nonNull, function, Integer::parseInt, map(env.serverMeta()),
                    PARAM_ERROR_HANDLER);
        }
        return value;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        return rangeBeforeBind(type, Integer.TYPE, Object::toString, nonNull);
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
