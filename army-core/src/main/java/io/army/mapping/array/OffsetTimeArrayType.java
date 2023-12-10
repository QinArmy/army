package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.OffsetTimeType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._TimeUtils;

import java.time.OffsetTime;
import java.util.function.Consumer;

public final class OffsetTimeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static OffsetTimeArrayType from(final Class<?> arrayClass) {
        final OffsetTimeArrayType instance;
        if (arrayClass == OffsetTime[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(OffsetTimeArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == OffsetTime.class) {
            instance = new OffsetTimeArrayType(arrayClass);
        } else {
            throw errorJavaType(OffsetTimeArrayType.class, arrayClass);
        }
        return instance;
    }

    public static OffsetTimeArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final OffsetTimeArrayType LINEAR = new OffsetTimeArrayType(OffsetTime[].class);

    public static final OffsetTimeArrayType UNLIMITED = new OffsetTimeArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private OffsetTimeArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return OffsetTime.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == OffsetTime[].class) {
            instance = OffsetTimeType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.TIMETZ_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                OffsetTimeArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, OffsetTimeArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                OffsetTimeArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }


    /*-------------------below static methods -------------------*/

    private static OffsetTime parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }
        return OffsetTime.parse(timeStr, _TimeUtils.OFFSET_TIME_FORMATTER_6);
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof OffsetTime)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final String doubleQuote;
        doubleQuote = String.valueOf(_Constant.DOUBLE_QUOTE);

        appender.accept(doubleQuote);
        appender.accept(((OffsetTime) element).format(_TimeUtils.OFFSET_TIME_FORMATTER_6));
        appender.accept(doubleQuote);

    }

}