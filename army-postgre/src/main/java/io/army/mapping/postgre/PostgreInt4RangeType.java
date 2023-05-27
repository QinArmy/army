package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

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

    public static <R> PostgreInt4RangeType fromFunc(final Class<? extends R> javaType,
                                                    final RangeFunction<Integer, R> function) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        Objects.requireNonNull(function);
        return new PostgreInt4RangeType(javaType, function);
    }

    /**
     * <p>
     * factory method example:
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
    public static PostgreInt4RangeType fromMethod(final Class<?> javaType, final String methodName) {
        if (javaType.isPrimitive() || javaType.isArray()) {
            throw errorJavaType(PostgreInt4RangeType.class, javaType);
        }
        return new PostgreInt4RangeType(javaType, createRangeFunction(javaType, Integer.TYPE, methodName));
    }

    /**
     * package method
     */
    static PostgreInt4RangeType fromArrayType(final PostgreInt4RangeArrayType type) {
        final Class<?> javaType;
        javaType = type.javaType.getComponentType();
        assert !javaType.isArray();
        final RangeFunction<Integer, ?> function = type.function;
        assert function != null;
        return new PostgreInt4RangeType(javaType, function);
    }

    public static final PostgreInt4RangeType TEXT = new PostgreInt4RangeType(String.class, null);


    private final RangeFunction<Integer, ?> function;

    /**
     * private constructor
     */
    private PostgreInt4RangeType(final Class<?> javaType, final @Nullable RangeFunction<Integer, ?> function) {
        super(javaType);
        assert function != null || javaType == String.class;
        this.function = function;
    }


    @Override
    public MappingType arrayTypeOfThis() {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> function = this.function;
        final MappingType arrayType;
        if (function == null) {
            assert javaType == String.class;
            arrayType = PostgreInt4RangeArrayType.TEXT_LINEAR;
        } else {
            arrayType = PostgreInt4RangeArrayType.fromFunc(_ArrayUtils.arrayClassOf(javaType), function);
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
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        final SqlType type;
        type = this.map(env.serverMeta());

        final RangeFunction<Integer, ?> function = this.function;
        final Object value;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = nonNull;
        } else if (function == null) {
            assert this.javaType == String.class;
            value = nonNull;
        } else if (EMPTY.equalsIgnoreCase((String) nonNull)) {
            value = emptyRange(this.javaType);
        } else {
            try {
                value = textToRange((String) nonNull, 0, ((String) nonNull).length(), function, Integer::parseInt);
            } catch (Exception e) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, e);
            }
        }
        return value;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        return rangeBeforeBind(type, Integer.TYPE, Object::toString, nonNull);
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        final RangeFunction<Integer, ?> function = this.function;
        final Object value;
        if (function == null) {
            assert this.javaType == String.class;
            value = nonNull;
        } else if (EMPTY.equalsIgnoreCase((String) nonNull)) {
            value = emptyRange(this.javaType);
        } else {
            try {
                value = textToRange((String) nonNull, 0, ((String) nonNull).length(), function, Integer::parseInt);
            } catch (Exception e) {
                throw ACCESS_ERROR_HANDLER.apply(this, type, nonNull, e);
            }
        }
        return value;
    }


}
