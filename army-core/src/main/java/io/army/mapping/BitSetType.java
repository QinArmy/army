package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.util.BitSet;

/**
 * <p>
 * This class is mapping class of {@link BitSet}.
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
            value = BitSet.valueOf(new long[]{v & 0Xffff_ffffL});
        } else if (nonNull instanceof long[]) {
            value = BitSet.valueOf((long[]) nonNull);
        } else if (nonNull instanceof byte[]) {
            value = BitSet.valueOf((byte[]) nonNull);
        } else {
            //TODO consider String
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
    public BitSet afterGet(SqlType type, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
