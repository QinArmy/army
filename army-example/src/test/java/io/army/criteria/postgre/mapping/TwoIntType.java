package io.army.criteria.postgre.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.postgre.type.TwoInt;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import java.util.List;

public final class TwoIntType extends MappingType
        implements MappingType.SqlUserDefinedType, MappingType.SqlCompositeType {

    public static final TwoIntType INSTANCE = new TwoIntType();

    public static TwoIntType from(final Class<?> javaType) {
        if (javaType != TwoInt.class) {
            throw errorJavaType(TwoIntType.class, javaType);
        }
        return INSTANCE;
    }

    private static final List<CompositeTypeField> FIELD_LIST = ArrayUtils.of(
            CompositeTypeField.from("a", IntegerType.INSTANCE),
            CompositeTypeField.from("b", IntegerType.INSTANCE)
    );

    private TwoIntType() {
    }

    @Override
    public Class<?> javaType() {
        return TwoInt.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreSqlType.USER_DEFINED;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public TwoInt convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof TwoInt)) {
            throw PARAM_ERROR_HANDLER.apply(this, this.map(env.serverMeta()), nonNull, null);
        }
        return (TwoInt) nonNull;
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/rowtypes.html#id-1.5.7.24.6">Constructing Composite Values</a>
     */
    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof TwoInt)) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        final TwoInt row = (TwoInt) nonNull;
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(_Constant.LEFT_PAREN);

        Object value;
        value = row.getFirst();
        if (value != null) {
            builder.append(value);
        }
        builder.append(_Constant.COMMA);

        value = row.getSecond();
        if (value != null) {
            builder.append(value);
        }
        return builder.append(_Constant.RIGHT_PAREN)
                .toString();
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String sqlTypeName(final ServerMeta meta) {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return "TWOINTS";
    }

    @Override
    public List<CompositeTypeField> fieldList() {
        return FIELD_LIST;
    }


}
