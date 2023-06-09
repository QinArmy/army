package io.army.criteria.postgre.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.postgre.type.MyRow;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
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

    private static final List<CompositeTypeField> FIELD_LIST = ArrayUtils.asUnmodifiableList(
            CompositeTypeField.from("a", IntegerType.INSTANCE),
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
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.Postgre) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreSqlType.USER_DEFINED;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public MyRow convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof MyRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, this.map(env.serverMeta()), nonNull, null);
        }
        return (MyRow) nonNull;
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/rowtypes.html#id-1.5.7.24.6">Constructing Composite Values</a>
     */
    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof MyRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        final MyRow row = (MyRow) nonNull;
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
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String sqlTypeName(final ServerMeta meta) {
        if (meta.dialectDatabase() != Database.Postgre) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return "MYROWTYPE";
    }

    @Override
    public List<CompositeTypeField> fieldList() {
        return FIELD_LIST;
    }


}
