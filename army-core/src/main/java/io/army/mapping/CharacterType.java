package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.util.BitSet;

public final class CharacterType extends _ArmyBuildInMapping implements MappingType.SqlStringType {


    public static final CharacterType INSTANCE = new CharacterType();

    public static CharacterType from(final Class<?> javaType) {
        if (javaType != Character.class) {
            throw errorJavaType(CharacterType.class, javaType);
        }
        return INSTANCE;
    }


    private CharacterType() {
    }

    @Override
    public Class<?> javaType() {
        return Character.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case MySQL:
                type = MySQLType.CHAR;
                break;
            case PostgreSQL:
                type = PostgreType.CHAR;
                break;
            case H2:
            case Oracle:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return type;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(final SqlType type, final MappingEnv env, final Object nonNull)
            throws CriteriaException {
        final String value;
        if (nonNull instanceof Character) {
            value = nonNull.toString();
        } else if (nonNull instanceof String) {
            if (((String) nonNull).length() != 1) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = ((String) nonNull).substring(0, 1);
        } else if (nonNull instanceof Number) {
            final String v;
            if (nonNull instanceof BigDecimal) {
                v = ((BigDecimal) nonNull).toPlainString();
            } else {
                v = nonNull.toString();
            }
            if (v.length() != 1) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = v.substring(0, 1);
        } else if (nonNull instanceof BitSet) {
            final BitSet v = (BitSet) nonNull;
            if (v.length() != 1) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            if (v.get(0)) {
                value = "1";
            } else {
                value = "0";
            }
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        return value;
    }

    @Override
    public Character afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


}
