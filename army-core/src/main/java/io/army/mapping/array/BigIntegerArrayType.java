package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.math.BigInteger;
import java.util.function.Consumer;

public class BigIntegerArrayType extends _ArmyNoInjectionMapping implements MappingType.SqlArrayType {

    public static BigIntegerArrayType from(final Class<?> javaType) {
        final BigIntegerArrayType instance;

        if (javaType == BigInteger[].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (!javaType.isArray()) {
            throw errorJavaType(BigIntegerArrayType.class, javaType);
        } else if (ArrayUtils.underlyingComponent(javaType) == BigInteger.class) {
            instance = new BigIntegerArrayType(javaType);
        } else {
            throw errorJavaType(BigIntegerArrayType.class, javaType);
        }
        return instance;
    }

    public static final BigIntegerArrayType UNLIMITED = new BigIntegerArrayType(Object.class);

    public static final BigIntegerArrayType LINEAR = new BigIntegerArrayType(BigInteger[].class);


    private final Class<?> javaType;

    /**
     * private constructor
     */
    private BigIntegerArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingElementJavaType() {
        return BigInteger.class;
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        return BigDecimalArrayType.mapToSqlType(this, meta);
    }

    @Override
    public final <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        if (targetType != String.class) {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return StringType.INSTANCE;
    }


    @Override
    public final MappingType elementType() {
        final MappingType instance;
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            instance = this;
        } else if (javaType == BigInteger[].class) {
            instance = BigIntegerType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public final Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false,
                BigIntegerArrayType::parseBigInteger, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, BigIntegerArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                BigIntegerArrayType::parseBigInteger, ACCESS_ERROR_HANDLER);
    }



    /*-------------------below static methods -------------------*/

    public static BigInteger parseBigInteger(String text, int offset, int end) {
        return new BigInteger(text.substring(offset, end));
    }

    public static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof BigInteger)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept(element.toString());
    }

}
