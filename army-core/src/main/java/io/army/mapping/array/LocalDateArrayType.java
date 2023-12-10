package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.LocalDateType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.time.LocalDate;
import java.util.function.Consumer;

public final class LocalDateArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {


    public static LocalDateArrayType from(final Class<?> arrayClass) {
        final LocalDateArrayType instance;
        if (arrayClass == LocalDate[].class) {
            instance = LINEAR;
        } else if (!arrayClass.isArray()) {
            throw errorJavaType(LocalDateArrayType.class, arrayClass);
        } else if (ArrayUtils.underlyingComponent(arrayClass) == LocalDate.class) {
            instance = new LocalDateArrayType(arrayClass);
        } else {
            throw errorJavaType(LocalDateArrayType.class, arrayClass);
        }
        return instance;
    }

    public static LocalDateArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final LocalDateArrayType LINEAR = new LocalDateArrayType(LocalDate[].class);

    public static final LocalDateArrayType UNLIMITED = new LocalDateArrayType(Object.class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private LocalDateArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return LocalDate.class;
    }

    @Override
    public MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == LocalDate[].class) {
            instance = LocalDateType.INSTANCE;
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
        return mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                LocalDateArrayType::parseText, PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, LocalDateArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                LocalDateArrayType::parseText, ACCESS_ERROR_HANDLER
        );
    }

    /*-------------------below static methods -------------------*/

    static SqlType mapToSqlType(final MappingType type, final ServerMeta meta) {
        final SqlType dataType;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                dataType = PostgreType.DATE_ARRAY;
                break;
            case MySQL:
            case SQLite:
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(type, meta);
        }
        return dataType;
    }

    private static LocalDate parseText(final String text, final int offset, final int end) {
        final String timeStr;
        if (text.charAt(offset) == _Constant.DOUBLE_QUOTE) {
            timeStr = text.substring(offset + 1, end - 1);
        } else {
            timeStr = text.substring(offset, end);
        }
        return LocalDate.parse(timeStr);
    }

    private static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof LocalDate)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        final String doubleQuote;
        doubleQuote = String.valueOf(_Constant.DOUBLE_QUOTE);

        appender.accept(doubleQuote);
        appender.accept(element.toString());
        appender.accept(doubleQuote);

    }


}