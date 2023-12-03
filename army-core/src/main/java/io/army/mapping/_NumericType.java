package io.army.mapping;


import io.army.sqltype.DataType;
import io.army.struct.CodeEnum;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>This class is base class class of below:
 *     <ul>
 *         <li>{@link BigDecimalType}</li>
 *         <li>{@link BigIntegerType}</li>
 *         <li>{@link ByteType}</li>
 *         <li>{@link DoubleType}</li>
 *         <li>{@link FloatType}</li>
 *         <li>{@link IntegerType}</li>
 *         <li>{@link LongType}</li>
 *         <li>{@link ShortType}</li>
 *         <li>{@link _UnsignedNumericType}</li>
 *     </ul>
 * </p>
 */
abstract class _NumericType extends _ArmyNoInjectionMapping {


    @Override
    public final <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        final MappingType type;
        if (targetType == String.class) {
            type = StringType.INSTANCE;
        } else if (targetType == Integer.class) {
            type = IntegerType.INSTANCE;
        } else if (targetType == Long.class) {
            type = LongType.BIGINT;
        } else if (targetType == BigDecimal.class) {
            type = BigDecimalType.INSTANCE;
        } else if (targetType == BigInteger.class) {
            type = BigIntegerType.INSTANCE;
        } else if (targetType == DoubleType.class) {
            type = DoubleType.INSTANCE;
        } else if (targetType == Float.class) {
            type = FloatType.INSTANCE;
        } else if (targetType == Short.class) {
            type = ShortType.INSTANCE;
        } else if (targetType == Byte.class) {
            type = ByteType.INSTANCE;
        } else if (this instanceof IntegerType
                && Enum.class.isAssignableFrom(targetType)
                && CodeEnum.class.isAssignableFrom(targetType)) {
            type = CodeEnumType.from(targetType);
        } else {
            throw noMatchCompatibleMapping(this, targetType);
        }
        return type;
    }

    /**
     * <p>This class is base class class of below:
     *     <ul>
     *         <li>{@link ByteType}</li>
     *         <li>{@link ShortType}</li>
     *         <li>{@link IntegerType}</li>
     *         <li>{@link LongType}</li>
     *         <li>{@link BigIntegerType}</li>
     *     </ul>
     * </p>
     */
    static abstract class _IntegerType extends _NumericType implements SqlIntegerType {

    }

    /**
     * <p>This class is base class class of below:
     *     <ul>
     *         <li>{@link FloatType}</li>
     *         <li>{@link DoubleType}</li>
     *     </ul>
     * </p>
     */
    static abstract class _FloatNumericType extends _NumericType implements SqlFloatType {

    }


    /**
     * <p>This class is base class class of below:
     *     <ul>
     *         <li>{@link UnsignedBigDecimalType}</li>
     *         <li>{@link _UnsignedIntegerType}</li>
     *     </ul>
     * </p>
     */
    static abstract class _UnsignedNumericType extends _NumericType implements SqlUnsignedNumberType {

    }

    /**
     * <p>This class is base class class of below:
     *     <ul>
     *
     *         <li>{@link UnsignedByteType}</li>
     *         <li>{@link UnsignedShortType}</li>
     *         <li>{@link UnsignedMediumIntType}</li>
     *         <li>{@link UnsignedIntegerType}</li>
     *         <li>{@link UnsignedLongType}</li>
     *         <li>{@link UnsignedBigIntegerType}</li>
     *     </ul>
     * </p>
     */
    static abstract class _UnsignedIntegerType extends _UnsignedNumericType implements SqlIntegerType {

    }


}
