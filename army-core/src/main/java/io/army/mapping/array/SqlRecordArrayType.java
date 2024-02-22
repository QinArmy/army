package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.LiteralParser;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.env.EscapeMode;
import io.army.function.TextFunction;
import io.army.mapping.*;
import io.army.mapping.optional.SqlRecordType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.ArraySqlRecord;
import io.army.type.SqlRecord;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


/**
 * @see SqlRecord
 * @see io.army.mapping.optional.SqlRecordType
 */
public class SqlRecordArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    public static SqlRecordArrayType fromColumn(final Class<?> arrayType, final MappingType columnType) {
        Objects.requireNonNull(columnType);
        final SqlRecordArrayType instance;
        if (arrayType.isArray() && ArrayUtils.underlyingComponent(arrayType) == SqlRecord.class) {
            instance = new SqlRecordArrayType(arrayType, Collections.singletonList(columnType));
        } else {
            throw errorJavaType(SqlRecordArrayType.class, arrayType);
        }
        return instance;
    }


    public static SqlRecordArrayType fromRow(final Class<?> arrayType, final List<MappingType> columnTypeList) {
        final SqlRecordArrayType instance;
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        } else if (arrayType.isArray() && ArrayUtils.underlyingComponent(arrayType) == SqlRecord.class) {
            instance = new SqlRecordArrayType(arrayType, _Collections.asUnmodifiableList(columnTypeList));
        } else {
            throw errorJavaType(SqlRecordArrayType.class, arrayType);
        }
        return instance;
    }


    public static SqlRecordArrayType fromList(final @Nullable Supplier<List<SqlRecord>> constructor,
                                              final List<MappingType> columnTypeList) {
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        }
        return new ListType(columnTypeList, constructor);
    }


    public static MappingType elementTypeOf(final SqlRecordArrayType arrayType) {
        final Class<?> javaType = arrayType.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = arrayType;
        } else if (javaType == SqlRecord[].class || arrayType instanceof ListType) {
            instance = SqlRecordType.fromRow(arrayType.columnTypeList);
        } else {
            instance = new SqlRecordArrayType(javaType.getComponentType(), arrayType.columnTypeList);
        }
        return instance;
    }

    public static SqlRecordArrayType arrayTypeOf(final SqlRecordArrayType arrayType) {
        final Class<?> javaType = arrayType.javaType;
        final SqlRecordArrayType instance;
        if (javaType == Object.class) { // unlimited dimension array
            instance = arrayType;
        } else if (arrayType instanceof ListType) {
            instance = new SqlRecordArrayType(SqlRecord[][].class, arrayType.columnTypeList);
        } else {
            instance = new SqlRecordArrayType(ArrayUtils.arrayClassOf(javaType), arrayType.columnTypeList);
        }
        return instance;
    }

    public static SqlRecordArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final SqlRecordArrayType UNLIMITED = new SqlRecordArrayType(Object.class, Collections.emptyList());


    private final Class<?> javaType;

    private final List<MappingType> columnTypeList;

    /**
     * private constructor
     */
    private SqlRecordArrayType(Class<?> javaType, List<MappingType> columnTypeList) {
        this.javaType = javaType;
        this.columnTypeList = columnTypeList;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final Class<?> underlyingJavaType() {
        return SqlRecord.class;
    }

    @Override
    public final MappingType elementType() {
        return elementTypeOf(this);
    }

    @Override
    public final MappingType arrayTypeOfThis() throws CriteriaException {
        return arrayTypeOf(this);
    }

    @Override
    public final DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.RECORD_ARRAY;
    }

    @Override
    public final Object convert(MappingEnv env, Object source) throws CriteriaException {
        if (this == UNLIMITED) {
            throw errorUseCase();
        }
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PostgreArrays::decodeElement,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, final MappingEnv env, Object source) throws CriteriaException {
        if (this == UNLIMITED) {
            throw errorUseCase();
        }
        final BiConsumer<Object, StringBuilder> consumer;
        consumer = (o, c) -> appendToText(env, o, c);

        return PostgreArrays.arrayBeforeBind(source, consumer, dataType, this, PARAM_ERROR_HANDLER);
    }

    @Override
    public final Object afterGet(DataType dataType, final MappingEnv env, Object source) throws DataAccessException {
        final TextFunction<?> function;
        function = (text, offset, end) -> decodeElement(env, text, offset, end);
        return PostgreArrays.arrayAfterGet(this, dataType, source, false, function, ACCESS_ERROR_HANDLER);
    }


    private void appendToText(final MappingEnv env, final Object element, final StringBuilder builder) {
        if (!(element instanceof SqlRecord)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        final SqlRecord record = (SqlRecord) element;

        final List<MappingType> columnTypeList = this.columnTypeList;
        final int columnSize = record.size(), typeSize = columnTypeList.size();
        if (typeSize > 0 && typeSize != columnSize) {
            throw recordColumnCountNotMatch(record, columnSize, this);
        }

        final ServerMeta meta = env.serverMeta();
        final LiteralParser literalParser = env.literalParser();

        MappingType columnType;
        DataType dataType;
        Object column;

        final int startIndex = builder.length();

        builder.append(_Constant.LEFT_PAREN);
        boolean escapse = false;
        for (int i = 0; i < columnSize; i++) {
            if (i > 0) {
                builder.append(_Constant.COMMA);
            }
            column = record.get(i);
            if (column == null) {
                builder.append(_Constant.NULL);
                continue;
            }

            columnType = columnTypeList.get(i);
            dataType = columnType.map(meta);
            columnType.beforeBind(dataType, env, column);

            if (column == DOCUMENT_NULL_VALUE) {
                builder.append(_Constant.NULL);
                continue;
            }
            escapse |= literalParser.parse(columnType, column, EscapeMode.ARRAY_ELEMENT_PART, builder);
        }
        builder.append(_Constant.RIGHT_PAREN);

        if (escapse) {
            builder.insert(startIndex, _Constant.DOUBLE_QUOTE);
            builder.append(_Constant.DOUBLE_QUOTE);
        }

    }

    private SqlRecord decodeElement(final MappingEnv env, final String text, int offset, int end) {
        final boolean outerQuote = text.charAt(offset) == _Constant.DOUBLE_QUOTE;
        if (outerQuote) {
            if (text.charAt(end - 1) != _Constant.DOUBLE_QUOTE) {
                throw _Exceptions.arrayElementError();
            }
            offset++;
            end--;
        }

        final ServerMeta meta = env.serverMeta();
        final List<MappingType> columnTypeList = this.columnTypeList;
        final int columnSize = columnTypeList.size();

        final SqlRecord record = ArraySqlRecord.create(columnSize);
        final boolean unlimited = this == UNLIMITED;
        final int lastIndex = end - 1;

        DataType dataType;
        MappingType columnType;
        Object columnValue;
        String elementText;
        boolean inDoubleQuote = false, recordEnd = false;
        int leftParenCount = 0;
        char ch;
        for (int i = offset, startIndex = -1, columnIndex = 0, endIndex; i < end; i++) {
            ch = text.charAt(i);
            if (inDoubleQuote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                }
            } else if (outerQuote && ch == _Constant.BACK_SLASH) {
                if (i < lastIndex && text.charAt(i + 1) != _Constant.DOUBLE_QUOTE) {
                    i++;
                }
            } else if (ch == _Constant.DOUBLE_QUOTE) {
                inDoubleQuote = true;
            } else if (ch == _Constant.LEFT_PAREN) {
                if (recordEnd) {
                    throw _Exceptions.arrayElementError();
                }
                leftParenCount++;
            } else if (leftParenCount == 0) {
                throw _Exceptions.arrayElementError();
            } else if (startIndex < 0) {
                if (!Character.isWhitespace(ch)) {
                    startIndex = i;
                }
            } else if (leftParenCount > 1) {
                if (ch == _Constant.RIGHT_PAREN) {
                    leftParenCount--;
                }
            } else if (leftParenCount == 1 && (ch == _Constant.COMMA || ch == _Constant.RIGHT_PAREN)) {
                endIndex = i;
                for (; endIndex > startIndex; endIndex--) {
                    if (!Character.isWhitespace(ch)) {
                        break;
                    }

                } // inner loop for

                if (columnIndex >= columnSize) {
                    throw columnSizeNotMatch(columnIndex + 1, columnSize);
                }

                elementText = text.substring(startIndex, endIndex + 1);

                if (_Constant.NULL.equalsIgnoreCase(elementText)) {
                    columnValue = null;
                } else {
                    if (unlimited) {
                        columnType = ObjectType.INSTANCE;
                    } else {
                        columnType = columnTypeList.get(columnIndex++);
                    }

                    dataType = columnType.map(meta);
                    columnValue = columnType.afterGet(dataType, env, elementText);
                    if (columnValue == DOCUMENT_NULL_VALUE) {
                        columnValue = null;
                    }
                }

                if (ch == _Constant.RIGHT_PAREN) {
                    recordEnd = true;
                }
                record.add(columnValue);

            } // else if

        } // outer loop for

        if (leftParenCount != 1 || inDoubleQuote) {
            throw _Exceptions.arrayElementError();
        }
        return record;
    }


    private static IllegalArgumentException recordColumnCountNotMatch(SqlRecord record, int columnSize, MappingType type) {
        String m = String.format("%s column count[%s] and column count[%s] of %s not match",
                record.getClass().getName(), record.size(), columnSize, type.getClass().getName());
        return new IllegalArgumentException(m);
    }

    private static IllegalArgumentException columnSizeNotMatch(int recordColumnSize, int columnSize) {
        String m = String.format("record column size[%s] and column size[%s] of %s not match.", recordColumnSize,
                columnSize, SqlRecordArrayType.class.getName());
        return new IllegalArgumentException(m);
    }


    private static CriteriaException errorUseCase() {
        String m = String.format("%s.UNLIMITED only can use read column from database", SqlRecordArrayType.class.getName());
        return new CriteriaException(m);
    }


    private static final class ListType extends SqlRecordArrayType
            implements UnaryGenericsMapping.ListMapping<SqlRecord> {

        private final Supplier<List<SqlRecord>> constructor;

        private ListType(List<MappingType> columnTypeList, @Nullable Supplier<List<SqlRecord>> constructor) {
            super(List.class, columnTypeList);
            this.constructor = constructor;
        }

        @Override
        public Class<SqlRecord> genericsType() {
            return SqlRecord.class;
        }

        @Override
        public Supplier<List<SqlRecord>> listConstructor() {
            return this.constructor;
        }


    } // ListType


}
