package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.util.Objects;


/**
 * <p>
 * This class representing Postgre int4range array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public final class PostgreInt4RangeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


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

    public static PostgreInt4RangeArrayType fromFunc(final Class<?> javaType,
                                                     final _RangeFunction<Integer, ?> function) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreInt4RangeArrayType(javaType, function);
    }

    /**
     * <p>
     * <pre><bre/>
     *    public static MyInt4Range create(int lowerBound,boolean includeLowerBound,int upperBound,boolean includeUpperBound){
     *        // do something
     *    }
     *     </pre>
     * </p>
     *
     * @param methodName public static factory method name,for example : com.my.Factory#create
     * @throws io.army.meta.MetaException throw when factory method name error.
     */
    public static PostgreInt4RangeArrayType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || !javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }

        return new PostgreInt4RangeArrayType(javaType,
                PostgreRangeType.createRangeFunction(_ArrayUtils.underlyingComponent(javaType), Integer.TYPE, methodName)
        );
    }

    public static final PostgreInt4RangeArrayType LINEAR = new PostgreInt4RangeArrayType(String[].class, null);

    final Class<?> javaType;

    final _RangeFunction<Integer, ?> function;

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
        return fromFunc(_ArrayUtils.arrayClassOf(this.javaType), this.function);
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final _RangeFunction<Integer, ?> function = this.function;

        final int dimension;
        dimension = _ArrayUtils.dimensionOf(javaType);
        final MappingType type;
        if (dimension > 1) {
            assert function != null;
            type = fromFunc(javaType.getComponentType(), function);
        } else if (function == null) {
            assert javaType == String.class;
            type = PostgreInt4RangeType.TEXT;
        } else {
            type = PostgreInt4RangeType.fromArrayType(this);
        }
        return type;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4RANGE_ARRAY;
    }

    @Override
    public Object convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull);
            }
            value = nonNull;
        } else if ((length = (text = (String) nonNull).length()) < 7) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), nonNull);
        } else {
            value = text;
        }
        return value;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!(this.javaType.isInstance(nonNull))) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
            }
            value = nonNull;
        } else if ((length = (text = (String) nonNull).length()) < 7) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull);
        } else {
            value = text;
        }
        return value;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }


}
