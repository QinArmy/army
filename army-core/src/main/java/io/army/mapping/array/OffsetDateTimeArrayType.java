package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.OffsetDateTimeType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._TimeUtils;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

public final class OffsetDateTimeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static OffsetDateTimeArrayType from(final Class<?> arrayClass) {
        final OffsetDateTimeArrayType instance;
        if (arrayClass == OffsetDateTime[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(OffsetDateTimeArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == OffsetDateTime.class) {
            instance = new OffsetDateTimeArrayType(arrayClass);
        } else {
            throw errorJavaType(OffsetDateTimeArrayType.class, arrayClass);
        }
        return instance;
    }

    public static OffsetDateTimeArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final OffsetDateTimeArrayType LINEAR = new OffsetDateTimeArrayType(OffsetDateTime[].class);

    public static final OffsetDateTimeArrayType UNLIMITED = new OffsetDateTimeArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private OffsetDateTimeArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return OffsetDateTime.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == OffsetDateTime[].class) {
            instance = OffsetDateTimeType.INSTANCE;
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
                dataType = PostgreType.TIMESTAMPTZ_ARRAY;
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
                OffsetDateTimeArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, OffsetDateTimeArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                OffsetDateTimeArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }


    /*-------------------below static methods -------------------*/

    private static OffsetDateTime parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }
        return OffsetDateTime.parse(timeStr, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof OffsetDateTime)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final String doubleQuote;
        doubleQuote = String.valueOf(_Constant.DOUBLE_QUOTE);

        appender.accept(doubleQuote);
        appender.accept(((OffsetDateTime) element).format(_TimeUtils.OFFSET_DATETIME_FORMATTER_6));
        appender.accept(doubleQuote);

    }


}
