package io.army.criteria.postgre.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.postgre.type.TwoInt;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.util.List;

public final class TwoIntType extends MappingType
        implements MappingType.SqlUserDefinedType, MappingType.SqlCompositeType {


    public static TwoIntType from(final Class<?> javaType) {
        if (javaType != TwoInt.class) {
            throw errorJavaType(TwoIntType.class, javaType);
        }
        return INSTANCE;
    }

    public static final TwoIntType INSTANCE = new TwoIntType();
    private static final DataType DATA_TYPE = DataType.from("TWOINTS");

    private static final List<CompositeTypeField> FIELD_LIST = ArrayUtils.of(
            CompositeTypeField.from("a", IntegerType.INTEGER),
            CompositeTypeField.from("b", IntegerType.INTEGER)
    );

    private TwoIntType() {
    }

    @Override
    public Class<?> javaType() {
        return TwoInt.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return DATA_TYPE;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public TwoInt convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof TwoInt)) {
            throw PARAM_ERROR_HANDLER.apply(this, this.map(env.serverMeta()), source, null);
        }
        return (TwoInt) source;
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/rowtypes.html#id-1.5.7.24.6">Constructing Composite Values</a>
     */
    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        if (!(source instanceof TwoInt)) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final TwoInt row = (TwoInt) source;
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
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CompositeTypeField> fieldList() {
        return FIELD_LIST;
    }


}
