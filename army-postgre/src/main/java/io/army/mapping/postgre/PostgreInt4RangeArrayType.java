package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;
import io.army.util._Collections;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * <p>
 * This class representing Postgre int4range array type {@link MappingType}
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">int4range</a>
 */
public class PostgreInt4RangeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static PostgreInt4RangeArrayType from(final Class<?> javaType) {
        final PostgreInt4RangeArrayType instance;
        if (javaType == String[].class) {
            instance = TEXT_LINEAR;
        } else if (javaType.isArray() && _ArrayUtils.underlyingComponent(javaType) == String.class) {
            instance = new PostgreInt4RangeArrayType(javaType, null);
        } else {
            throw errorJavaType(PostgreInt4RangeArrayType.class, javaType);
        }
        return instance;
    }

    public static PostgreInt4RangeArrayType fromFunc(final Class<?> javaType,
                                                     final RangeFunction<Integer, ?> function) {
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

    public static final PostgreInt4RangeArrayType TEXT_LINEAR = new PostgreInt4RangeArrayType(String[].class, null);

    final Class<?> javaType;

    final RangeFunction<Integer, ?> function;

    private PostgreInt4RangeArrayType(final Class<?> javaType, final @Nullable RangeFunction<Integer, ?> function) {
        assert function != null
                || javaType == String[].class
                || _ArrayUtils.underlyingComponent(javaType) == String.class;
        this.javaType = javaType;
        this.function = function;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final MappingType arrayTypeOfThis() {
        return fromFunc(_ArrayUtils.arrayClassOf(this.javaType), this.function);
    }

    @Override
    public final MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final RangeFunction<Integer, ?> function = this.function;

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
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4RANGE_ARRAY;
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public final Object convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        final SqlType type;
        type = map(env.serverMeta());
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = (String) nonNull).length()) < 7) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else {
            value = this.textToRangeArray((String) nonNull, type, PARAM_ERROR_HANDLER);
        }
        return value;
    }

    @Override
    public final Object beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        final Object value;
        final String text;
        final int length;

        if (!(nonNull instanceof String)) {
            if (!(this.javaType.isInstance(nonNull))) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = null; //TODO
        } else if ((length = (text = (String) nonNull).length()) < 2) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else {
            value = text;
        }
        return value;
    }


    @Override
    public final Object afterGet(SqlType type, MappingEnv env, final Object nonNull) throws DataAccessException {
        if (!(nonNull instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        return this.textToRangeArray((String) nonNull, type, ACCESS_ERROR_HANDLER);
    }


    private Object parseInt4RangeArray(final String text, final int offset, final int end) {
        return PostgreRangeType.textToNonEmptyRange(text, offset, end, this.function, Integer::parseInt);
    }

    private Object textToRangeArray(final String text, final SqlType type, final ErrorHandler handler) {
        final PostgreArrayParsers.ElementFunction elementFunction;
        if (this.function == null) {
            assert this.javaType == String.class;
            elementFunction = String::substring;
        } else {
            elementFunction = this::parseInt4RangeArray;
        }
        final Class<?> arrayJavaType;

        if (this instanceof ListType) {
            final Class<?> elementType;
            elementType = ((ListType) this).elementTypeList.get(0);
            arrayJavaType = _ArrayUtils.arrayClassOf(elementType);
        } else {
            arrayJavaType = this.javaType;
        }

        final Object array, value;

        try {
            array = PostgreArrayParsers.parseArrayText(arrayJavaType, text, _Constant.COMMA, elementFunction);
        } catch (Throwable e) {
            throw handler.apply(this, type, text, e);
        }
        if (this instanceof ListType) {
            value = PostgreArrayParsers.linearToList(array, ((ListType) this).supplier);
        } else {
            value = array;
        }
        return value;
    }

    private static final class ListType extends PostgreInt4RangeArrayType implements ElementMappingType {

        private final Supplier<List<Object>> supplier;

        private final List<Class<?>> elementTypeList;

        private ListType(Class<?> javaType, Supplier<List<Object>> supplier,
                         Class<?> elementType, RangeFunction<Integer, ?> function) {
            super(javaType, function);
            this.supplier = supplier;
            this.elementTypeList = _Collections.singletonList(elementType);
        }

        @Override
        public List<Class<?>> elementTypes() {
            return this.elementTypeList;
        }


    }


}
