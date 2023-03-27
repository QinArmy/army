package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * <p>
 * This class is mapping class of {@link BitSet}.
 * This mapping type can convert below java type:
 * <ul>
 *     <li>{@link Byte},non-empty</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link String} , it must be bit string.</li>
 *     <li>{@code  byte[]}, non-empty</li>
 *     <li>{@code long[]}, non-empty</li>
 * </ul>
 *  to {@link BitSet}
 * </p>
 *
 * @since 1.0
 */
public final class BitSetType extends _ArmyNoInjectionMapping {

    public static final BitSetType INSTANCE = new BitSetType();

    public static BitSetType from(Class<?> fieldType) {
        if (fieldType != BitSet.class) {
            throw errorJavaType(BitSetType.class, fieldType);
        }
        return INSTANCE;
    }

    private BitSetType() {
    }

    @Override
    public Class<?> javaType() {
        return BitSet.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.BIT;
                break;
            case PostgreSQL:
                type = PostgreType.VARBIT;
                break;
            default:
                throw noMappingError(meta);

        }
        return type;
    }

    @Override
    public BitSet convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        final BitSet value;
        if (nonNull instanceof BitSet) {
            value = (BitSet) nonNull;
        } else if (nonNull instanceof Long) {
            value = BitSet.valueOf(new long[]{(Long) nonNull});
        } else if (nonNull instanceof Integer) {
            final int v = (Integer) nonNull;
            value = BitSet.valueOf(new long[]{v & 0xffff_ffffL});
        } else if (nonNull instanceof Short) {
            final short v = (Short) nonNull;
            value = BitSet.valueOf(new long[]{v & 0xffffL});
        } else if (nonNull instanceof Byte) {
            value = BitSet.valueOf(new byte[]{(Byte) nonNull});
        } else if (nonNull instanceof BigInteger) {
            value = bitStringToBitSet(((BigInteger) nonNull).toString(2));
        } else if (nonNull instanceof long[]) {
            value = BitSet.valueOf((long[]) nonNull);
        } else if (nonNull instanceof byte[]) {
            value = BitSet.valueOf((byte[]) nonNull);
        } else if (nonNull instanceof String) {
            try {
                value = bitStringToBitSet((String) nonNull);
            } catch (IllegalArgumentException e) {
                String m = String.format("non-bit string couldn't convert to %s .", BitSet.class.getName());
                throw new CriteriaException(m, e);
            }
        } else {
            throw dontSupportConvertType(nonNull);
        }
        return value;
    }

    @Override
    public Object beforeBind(final SqlType type, final MappingEnv env, final Object nonNull) {
        final Object value;
        switch (type.database()) {
            case MySQL:
                value = _MappingUtils.bitwiseToLong(type, nonNull);
                break;
            case PostgreSQL:
                value = _MappingUtils.bitwiseToString(type, nonNull);
                break;
            default:
                throw outRangeOfSqlType(type, nonNull);
        }
        return value;
    }

    @Override
    public BitSet afterGet(final SqlType type, final MappingEnv env, final Object nonNull) {
        final BitSet value;
        switch (type.database()) {
            case MySQL: {
                if (!(nonNull instanceof Long)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                value = BitSet.valueOf(new long[]{(Long) nonNull});
            }
            break;
            case PostgreSQL: {
                if (!(nonNull instanceof String)) {
                    throw errorJavaTypeForSqlType(type, nonNull);
                }
                try {
                    value = bitStringToBitSet((String) nonNull);
                } catch (IllegalArgumentException e) {
                    throw errorValueForSqlType(type, nonNull, e);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(type.database());
        }

        return value;
    }


    private static BitSet bitStringToBitSet(final String bitStr) throws IllegalArgumentException {
        final int bitLength;
        bitLength = bitStr.length();
        if (bitLength == 0) {
            throw new IllegalArgumentException("bit string must non-empty.");
        }
        final BitSet bitSet = new BitSet(bitLength);
        final int maxIndex = bitLength - 1;
        for (int i = 0; i < bitLength; i++) {
            switch (bitStr.charAt(maxIndex - i)) {
                case '0':
                    bitSet.set(i, false);
                    break;
                case '1':
                    bitSet.set(i, true);
                    break;
                default:
                    throw new IllegalArgumentException("non-bit string");
            }
        }
        return bitSet;
    }


}
