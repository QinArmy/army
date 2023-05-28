package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @since 1.0
 */
abstract class NumberArrayType extends _ArmyNoInjectionMapping
        implements MappingType.SqlArrayType, MappingType.SqlNumberType {


    private final Class<?> javaType;

    private final Class<?> underlyingType;


    /**
     * <p>
     * Package constructor ,and must be Package constructor.
     * </p>
     */
    NumberArrayType() {
        this.javaType = Object.class;
        this.underlyingType = Object.class;
    }

    /**
     * <p>
     * Package constructor ,and must be Package constructor.
     * </p>
     */
    NumberArrayType(Class<?> javaType) {
        this.javaType = javaType;
        this.underlyingType = _ArrayUtils.underlyingComponent(javaType);
        assert Number.class.isAssignableFrom(this.underlyingType);
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public MappingType elementType() {
        return null;
    }

    @Override
    public final Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public final Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public final Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }


    private void appendPostgreArray(final Object array, final StringBuilder builder, final SqlType type,
                                    final BiPredicate<Object, StringBuilder> valueHandler) {
        final int arrayDimension;
        arrayDimension = _ArrayUtils.dimensionOf(array.getClass());

        builder.append('{');
        Object component;

        for (int dimension = arrayDimension; dimension > 0; dimension--) {

            final int length;
            length = Array.getLength(array);
            for (int index = 0; index < length; index++) {
                if (index > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    builder.append(_Constant.SPACE);
                }
                component = Array.get(array, index);
                if (component == null) {
                    builder.append(_Constant.NULL);
                } else if (valueHandler.test(component, builder)) {
                    throw PARAM_ERROR_HANDLER.apply(this, type, array, null);
                }
            }
        }

        builder.append('}');
    }

    static void appendPostgreNumberArray(final Number[] array, final StringBuilder builder) {
        builder.append('{');
        Number element;
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                builder.append(_Constant.SPACE);
            }
            element = array[i];
            if (element == null) {
                builder.append(_Constant.NULL);
            } else if (element instanceof BigDecimal) {
                builder.append(((BigDecimal) element).toPlainString());
            } else {
                builder.append(element);
            }
        }
        builder.append('}');
    }

    private void appendPostgreNumberList(final List<?> list, final StringBuilder builder, final SqlType type,
                                         final Class<? extends Number> numberClass) {
        builder.append('{');
        int index = 0;
        for (Object element : list) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                builder.append(_Constant.SPACE);
            }
            if (element == null) {
                builder.append(_Constant.NULL);
            } else if (!numberClass.isInstance(element)) {
                throw PARAM_ERROR_HANDLER.apply(this, type, list, null);
            } else if (element instanceof BigDecimal) {
                builder.append(((BigDecimal) element).toPlainString());
            } else if (element instanceof Number) {
                builder.append(element);
            }
            index++;
        }

        builder.append('}');

    }


}
