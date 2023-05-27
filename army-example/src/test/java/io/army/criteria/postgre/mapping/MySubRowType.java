package io.army.criteria.postgre.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.postgre.type.MySubRow;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;

import java.util.List;

public final class MySubRowType extends MappingType
        implements MappingType.SqlUserDefinedType, MappingType.SqlCompositeType {


    public static final MySubRowType INSTANCE = new MySubRowType();

    public static MySubRowType from(final Class<?> javaType) {
        if (javaType != MySubRow.class) {
            throw errorJavaType(MySubRowType.class, javaType);
        }
        return INSTANCE;
    }

    private static final List<CompositeTypeField> FIELD_LIST = _ArrayUtils.asUnmodifiableList(
            CompositeTypeField.from("d", IntegerType.INSTANCE),
            CompositeTypeField.from("e", TextType.INSTANCE)
    );

    private MySubRowType() {
    }

    @Override
    public Class<?> javaType() {
        return MySubRow.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.USER_DEFINED;
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public MySubRow convert(MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof MySubRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, this.map(env.serverMeta()), nonNull, null);
        }
        return (MySubRow) nonNull;
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/rowtypes.html#id-1.5.7.24.6">Constructing Composite Values</a>
     */
    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof MySubRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        }
        final MySubRow row = (MySubRow) nonNull;
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(_Constant.LEFT_PAREN);

        Object value;
        value = row.getNumber();
        if (value != null) {
            builder.append(value);
        }
        builder.append(_Constant.COMMA);

        value = row.getText();
        if (value != null) {
            builder.append(_Constant.DOUBLE_QUOTE)
                    .append(value)
                    .append(_Constant.DOUBLE_QUOTE);
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
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return "SUBROWTYPE";
    }


    @Override
    public List<CompositeTypeField> fieldList() {
        return FIELD_LIST;
    }


}
