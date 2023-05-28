package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.SingleGenericsMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

public class PostgreInt4MultiRangeType extends PostgreMultiRangeType<Integer> {


    public static PostgreInt4MultiRangeType from(final Class<?> javaType) {
        final PostgreInt4MultiRangeType instance;
        final Class<?> componentType;
        if (javaType == String[].class) {
            instance = TEXT;
        } else if (!javaType.isArray() || (componentType = javaType.getComponentType()).isArray()) {

        }
        return null;
    }


    public static final PostgreInt4MultiRangeType TEXT = new PostgreInt4MultiRangeType(String.class, null);

    private PostgreInt4MultiRangeType(Class<?> javaType, @Nullable RangeFunction<Integer, ?> rangeFunc) {
        super(javaType, Integer.class, rangeFunc, Integer::parseInt);
    }

    @Override
    public final SqlType map(final ServerMeta meta) throws NotSupportDialectException {
        if (meta.dialectDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreDataType.INT4MULTIRANGE;
    }


    @Override
    public final Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        final SqlType type;
        type = map(env.serverMeta());
        final Object value;
        final String text;
        final int length;
        if (!(nonNull instanceof String)) {
            if (!this.javaType.isInstance(nonNull)) {
                throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
            }
            value = nonNull;
        } else if ((length = (text = (String) nonNull).length()) < 5) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(0) != _Constant.LEFT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else if (text.charAt(length - 1) != _Constant.RIGHT_BRACE) {
            throw PARAM_ERROR_HANDLER.apply(this, type, nonNull, null);
        } else {
            //PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, )
            value = null;
        }
        return value;
    }

    @Override
    public final String beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {

        return null;
    }

    @Override
    public final Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }

    @Override
    public final MappingType subtype() {
        return IntegerType.INSTANCE;
    }

    private static final class ListType extends PostgreInt4MultiRangeType implements SingleGenericsMapping<Object> {

        private final Class<Object> elementType;

        @SuppressWarnings("unchecked")
        private ListType(Class<?> javaType, Class<?> elementType, @Nullable RangeFunction<Integer, ?> rangeFunc) {
            super(javaType, rangeFunc);
            this.elementType = (Class<Object>) elementType;
        }

        @Override
        public Class<Object> genericsType() {
            return this.elementType;
        }


    }//ListType


}
