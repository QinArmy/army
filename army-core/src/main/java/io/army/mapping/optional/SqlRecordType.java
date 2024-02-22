package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.LiteralParser;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.env.EscapeMode;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.mapping.array.SqlRecordArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.SqlRecord;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * <p>This class mapping List to database record (for example : postgre record ,oid : 2249)
 *
 * @see io.army.type.SqlRecord
 * @see io.army.mapping.array.SqlRecordArrayType
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-type.html">Postgre pg_type table ,oid : 2249</a>
 */
public final class SqlRecordType extends _ArmyBuildInMapping implements MappingType.SqlRecordColumnType {

    public static SqlRecordType fromColumn(final MappingType columnType) {
        Objects.requireNonNull(columnType);
        return new SqlRecordType(Collections.singletonList(columnType));
    }


    public static SqlRecordType fromRow(final List<MappingType> columnTypeList) {
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        }
        return new SqlRecordType(_Collections.asUnmodifiableList(columnTypeList));
    }


    public static SqlRecordType fromUnlimited() {
        return UNLIMITED;
    }


    public static final SqlRecordType UNLIMITED = new SqlRecordType(Collections.emptyList());

    private final List<MappingType> columnTypeList;

    /**
     * private constructor
     */
    private SqlRecordType(List<MappingType> columnTypeList) {
        this.columnTypeList = columnTypeList;
    }

    @Override
    public Class<?> javaType() {
        return SqlRecord.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.RECORD;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final MappingType instance;
        switch (this.columnTypeList.size()) {
            case 0:
                instance = SqlRecordArrayType.UNLIMITED;
                break;
            case 1:
                instance = SqlRecordArrayType.fromColumn(SqlRecord[].class, this.columnTypeList.get(0));
                break;
            default:
                instance = SqlRecordArrayType.fromRow(SqlRecord[].class, this.columnTypeList);
        }
        return instance;
    }

    @Override
    public SqlRecord convert(MappingEnv env, Object source) throws CriteriaException {
        if (this == UNLIMITED) {
            if (!(source instanceof String) || isNotRecordText((String) source)) {
                throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
            }
        }
        return null;
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, final Object source) throws CriteriaException {
        final String value;
        if (this == UNLIMITED) {
            if (!(source instanceof String) || isNotRecordText((String) source)) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            value = (String) source;
        } else if (source instanceof SqlRecord) {
            final StringBuilder builder = new StringBuilder();
            appendRecordText(dataType, env, (SqlRecord) source, builder, this, this.columnTypeList, EscapeMode.DEFAULT);
            value = builder.toString();
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public SqlRecord afterGet(DataType dataType, MappingEnv env, final Object source) throws DataAccessException {
        return null;
    }


    public static void appendRecordText(final DataType dataType, final MappingEnv env, final SqlRecord record,
                                        final StringBuilder builder, final MappingType type, final List<MappingType> columnTypeList,
                                        final EscapeMode mode) {

        final int columnSize = record.size(), typeSize = columnTypeList.size();
        if (typeSize > 0 && typeSize != columnSize) {
            final IllegalArgumentException error;
            error = _Exceptions.recordColumnCountNotMatch(record, columnSize, type);
            if (type instanceof SqlRecordType) {
                throw PARAM_ERROR_HANDLER.apply(type, dataType, record, error);
            } else {
                throw error;
            }
        }

        final ServerMeta meta = env.serverMeta();
        final LiteralParser literalParser = env.literalParser();

        final int startIndex = builder.length();
        final boolean unlimited = typeSize == 0;

        MappingType columnType;
        DataType columnDataType;

        builder.append(_Constant.LEFT_PAREN);
        boolean escapse = false;
        int index = 0;
        for (Object column : record) {
            if (index > 0) {
                builder.append(_Constant.COMMA);
            }
            if (column == null) {
                builder.append(_Constant.NULL);
                index++;
                continue;
            }

            if (unlimited) {
                columnType = TextType.INSTANCE;
            } else {
                columnType = columnTypeList.get(index);
            }
            columnDataType = columnType.map(meta);
            columnType.beforeBind(columnDataType, env, column);

            if (column == DOCUMENT_NULL_VALUE) {
                builder.append(_Constant.NULL);
            } else {
                escapse |= literalParser.parse(columnType, column, mode, builder);
            }

            index++;

        } // loop for

        builder.append(_Constant.RIGHT_PAREN);

        if (escapse) {
            switch (mode) {
                case ARRAY_ELEMENT:
                case ARRAY_ELEMENT_PART:
                    builder.insert(startIndex, _Constant.DOUBLE_QUOTE);
                    builder.append(_Constant.DOUBLE_QUOTE);
                    break;
                default:
            }

        }

    }


    private static boolean isNotRecordText(final String source) {
        final int length = source.length();
        final boolean match;
        if (length < 3) {
            match = true;
        } else {
            match = source.charAt(0) != _Constant.LEFT_PAREN
                    || source.charAt(length - 1) != _Constant.RIGHT_PAREN;
        }
        return match;
    }


}
