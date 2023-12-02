package io.army.criteria.postgre.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.postgre.type.MyRow;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.util.List;

public final class MyRowType extends MappingType
        implements MappingType.SqlUserDefinedType, MappingType.SqlCompositeType {

    public static final MyRowType INSTANCE = new MyRowType();

    public static MyRowType from(final Class<?> javaType) {
        if (javaType != MyRow.class) {
            throw errorJavaType(MyRowType.class, javaType);
        }
        return INSTANCE;
    }

    private static final DataType DATA_TYPE = DataType.from("MYROWTYPE");

    private static final List<CompositeTypeField> FIELD_LIST = ArrayUtils.of(
            CompositeTypeField.from("a", IntegerType.INTEGER),
            CompositeTypeField.from("b", TextArrayType.LIST),
            CompositeTypeField.from("c", MySubRowType.INSTANCE)
    );

    private MyRowType() {
    }

    @Override
    public Class<?> javaType() {
        return MyRow.class;
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
    public MyRow convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof MyRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, this.map(env.serverMeta()), source, null);
        }
        return (MyRow) source;
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/rowtypes.html#id-1.5.7.24.6">Constructing Composite Values</a>
     */
    @Override
    public String beforeBind(DataType type, MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof MyRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, type, source, null);
        }
        final MyRow row = (MyRow) source;
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(_Constant.LEFT_PAREN);

        Object value;
        MappingType mappingType;
        value = row.getLevel();
        if (value != null) {
            builder.append(value);
        }
        builder.append(_Constant.COMMA);

        value = row.getTextList();
        if (value != null) {
            mappingType = TextArrayType.LIST;
            builder.append(mappingType.beforeBind(mappingType.map(env.serverMeta()), env, value));
        }
        builder.append(_Constant.COMMA);

        value = row.getSubRow();
        if (value != null) {
            mappingType = MySubRowType.INSTANCE;
            builder.append(mappingType.beforeBind(mappingType.map(env.serverMeta()), env, value));
        }
        return builder.append(_Constant.RIGHT_PAREN)
                .toString();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }


    @Override
    public List<CompositeTypeField> fieldList() {
        return FIELD_LIST;
    }


}
