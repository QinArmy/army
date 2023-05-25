package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * <p>
 * This class representing Postgre int4range array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public final class PostgreInt4RangeArrayType extends PostgreRangeType {


    public static PostgreInt4RangeArrayType from(final Class<?> javaType) {
        final PostgreInt4RangeArrayType instance;
        if (javaType == String[].class) {
            instance = LINEAR;
        } else if (javaType.isArray() && _ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt4RangeArrayType(javaType, null);
        } else {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        return instance;
    }

    public static PostgreInt4RangeArrayType func(final Class<?> javaType,
                                                 final _RangeFunction<Integer, ?> function) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreInt4RangeArrayType(javaType, function);
    }

    public static PostgreInt4RangeArrayType method(final Class<?> javaType, final Method method) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }

        return new PostgreInt4RangeArrayType(javaType,
                createRangeFunction(_ArrayUtils.underlyingComponent(javaType), Integer.TYPE, method)
        );
    }

    public static final PostgreInt4RangeArrayType LINEAR = new PostgreInt4RangeArrayType(String[].class, null);

    private final Class<?> javaType;

    private final _RangeFunction<Integer, ?> function;

    private PostgreInt4RangeArrayType(final Class<?> javaType, final @Nullable _RangeFunction<Integer, ?> function) {
        assert function != null
                || javaType == String[].class
                || _ArrayUtils.underlyingComponent(javaType) == String.class;
        this.javaType = javaType;
        this.function = function;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        return super.arrayTypeOfThis();
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }


}
