package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.LocalDateTimeType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._TimeUtils;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public final class LocalDateTimeArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static LocalDateTimeArrayType from(final Class<?> arrayClass) {
        final LocalDateTimeArrayType instance;
        if (arrayClass == LocalDateTime[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(LocalDateTimeArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == LocalDateTime.class) {
            instance = new LocalDateTimeArrayType(arrayClass);
        } else {
            throw errorJavaType(LocalDateTimeArrayType.class, arrayClass);
        }
        return instance;
    }

    public static LocalDateTimeArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final LocalDateTimeArrayType LINEAR = new LocalDateTimeArrayType(LocalDateTime[].class);

    public static final LocalDateTimeArrayType UNLIMITED = new LocalDateTimeArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private LocalDateTimeArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return LocalDateTime.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == LocalDateTime[].class) {
            instance = LocalDateTimeType.INSTANCE;
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
                dataType = PostgreType.TIMESTAMP_ARRAY;
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
                LocalDateTimeArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, LocalDateTimeArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                LocalDateTimeArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }

    /*-------------------below static methods -------------------*/

    private static LocalDateTime parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }
        return LocalDateTime.parse(timeStr, _TimeUtils.DATETIME_FORMATTER_6);
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof LocalDateTime)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final String doubleQuote;
        doubleQuote = String.valueOf(_Constant.DOUBLE_QUOTE);

        appender.accept(doubleQuote);
        appender.accept(((LocalDateTime) element).format(_TimeUtils.DATETIME_FORMATTER_6));
        appender.accept(doubleQuote);

    }


}