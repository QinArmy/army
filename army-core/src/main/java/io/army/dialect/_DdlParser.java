package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.mapping.NoCastTextType;
import io.army.meta.*;
import io.army.sqltype.SqlType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class _DdlParser<P extends _ArmyDialectParser> implements DdlDialect {

    protected static final String SPACE_UNSIGNED = " UNSIGNED";

    protected static final String SPACE_COMMENT = " COMMENT";

    protected final List<String> errorMsgList = _Collections.arrayList();

    protected final P parser;

    protected final ServerMeta serverMeta;

    protected _DdlParser(P parser) {
        this.parser = parser;
        this.serverMeta = parser.serverMeta;
    }

    @Override
    public final List<String> errorMsgList() {
        return this.errorMsgList;
    }

    @Override
    public final void dropTable(List<TableMeta<?>> tableList, List<String> sqlList) {
        final int size = tableList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(size * 10);
        builder.append("DROP TABLE IF EXISTS ");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            }
            this.parser.identifier(tableList.get(i).tableName(), builder);
        }
        sqlList.add(builder.toString());
    }

    @Override
    public final <T> void createTable(final TableMeta<T> table, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder(128)
                .append("CREATE TABLE IF NOT EXISTS ");

        this.parser.safeObjectName(table, builder)
                .append(_Constant.SPACE_LEFT_PAREN)
                .append("\n\t");

        final List<FieldMeta<T>> fieldList = table.fieldList();
        final int fieldSize = fieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                builder.append(" ,\n\t");
            }
            this.columnDefinition(fieldList.get(i), builder);
        }

        this.doAppendIndexInTableDef(table, builder);

        builder.append('\n')
                .append(_Constant.SPACE_RIGHT_PAREN);

        appendTableOption(table, builder);
        sqlList.add(builder.toString());

        switch (this.parser.database) {
            case Postgre: {
                appendIndexAfterTableDef(table, sqlList);
                appendOuterComment(table, sqlList);
            }
            break;
            case MySQL:
            case Oracle:
            case H2:
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.parser.database);
        }

    }


    @Override
    public final void addColumn(final List<FieldMeta<?>> fieldList, final List<String> sqlList) {
        final int fieldSize = fieldList.size();
        if (fieldSize == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(128)
                .append("ALTER TABLE ");

        TableMeta<?> table = null;
        for (int i = 0; i < fieldSize; i++) {
            final FieldMeta<?> field;
            field = fieldList.get(i);
            if (i > 0) {
                if (field.tableMeta() != table) {
                    throw new IllegalArgumentException("fieldList error");
                }
                builder.append(" ,\n\t");
            } else {
                table = field.tableMeta();
                this.parser.safeObjectName(table, builder)
                        .append(" ADD COLUMN (\n\t");
            }
            //TODO 新增的 时间类型列应该有默认值,否则 mysql 会以 00000-00-00 作为默认值.
            this.columnDefinition(field, builder);
        }
        builder.append("\n)");
        sqlList.add(builder.toString());
    }


    protected final void columnDefinition(final FieldMeta<?> field, final StringBuilder builder) {
        this.parser.safeObjectName(field, builder)
                .append(_Constant.SPACE);
        final SqlType sqlType;
        sqlType = field.mappingType().map(this.serverMeta);

        if (field.generatorType() == GeneratorType.POST) {
            this.postDataType(field, sqlType, builder);
        } else {
            this.dataType(field, sqlType, builder);
        }

        if (field.nullable()) {
            builder.append(_Constant.SPACE_NULL);
        } else {
            builder.append(_Constant.SPACE_NOT_NULL);
        }

        final String defaultValue = field.defaultValue();
        if (_StringUtils.hasText(defaultValue) && checkDefaultComplete(field, defaultValue)) {
            builder.append(" DEFAULT ")
                    .append(defaultValue);
        }

        if (field.generatorType() == GeneratorType.POST) {
            appendPostGenerator(field, builder);
        }
        switch (this.parser.database) {
            case MySQL:
                appendComment(field, builder);
                break;
            case H2:
            case Oracle:
            case Postgre:
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.parser.database);

        }

    }

    protected final void defaultStartWithWhiteSpace(FieldMeta<?> field) {
        this.errorMsgList.add(String.format("%s start with white space.", field));
    }


    protected abstract void dataType(FieldMeta<?> field, SqlType type, StringBuilder builder);

    protected abstract void postDataType(FieldMeta<?> field, SqlType type, StringBuilder builder);

    protected abstract void appendTableOption(final TableMeta<?> table, final StringBuilder builder);

    protected abstract void appendPostGenerator(final FieldMeta<?> field, final StringBuilder builder);

    protected void appendComment(final DatabaseObject object, final StringBuilder builder) {

        builder.append(SPACE_COMMENT)
                .append(_Constant.SPACE);
        this.parser.literal(NoCastTextType.INSTANCE, object.comment(), builder);
    }


    /**
     * non-static
     */
    protected final String COMMA_PRIMARY_KEY = " ,\n\tPRIMARY KEY";

    /**
     * non-static
     */
    protected final String COMMA_UNIQUE = " ,\n\tUNIQUE";

    /**
     * non-static
     */
    protected final String COMMA_INDEX = " ,\n\tINDEX";


    protected final <T> void appendIndexInTableDef(final IndexMeta<T> index, final StringBuilder builder) {
        if (index.isPrimaryKey()) {
            builder.append(COMMA_PRIMARY_KEY);
        } else if (index.isUnique()) {
            builder.append(COMMA_UNIQUE);
        } else {
            builder.append(COMMA_INDEX);
        }

        switch (this.parser.database) {
            case MySQL: {
                if (!index.isPrimaryKey()) {
                    builder.append(_Constant.SPACE);
                    this.parser.identifier(index.name(), builder);
                }
            }
            break;
            case Postgre:
            case Oracle:
            case H2:
                break;

            default:// no-op
        }

        switch (this.parser.database) {
            case MySQL:
                appendIndexType(index, builder);
                break;
            case Postgre:
            case H2:
            case Oracle:
            default://no-op
        }

        appendIndexFieldList(index, builder);

    }

    protected final <T> void appendIndexType(final IndexMeta<T> index, final StringBuilder builder) {
        final String type;
        type = index.type();
        if (_StringUtils.hasText(type)) {
            builder.append(" USING ");
            this.parser.identifier(index.type(), builder);
        }
    }


    protected final <T> void appendIndexFieldList(final IndexMeta<T> index, StringBuilder builder) {

        final List<IndexFieldMeta<T>> indexFieldList = index.fieldList();
        final int fieldSize = indexFieldList.size();
        IndexFieldMeta<T> field;
        Boolean asc;
        builder.append(_Constant.SPACE_LEFT_PAREN);// index left bracket

        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                builder.append(_Constant.SPACE);
            }
            field = indexFieldList.get(i);
            this.parser.safeObjectName(field, builder);

            asc = field.fieldAsc();
            if (asc == null) {
                continue;
            }
            if (asc) {
                builder.append(_Constant.SPACE_ASC);
            } else {
                builder.append(_Constant.SPACE_DESC);
            }
        }

        builder.append(_Constant.SPACE_RIGHT_PAREN); // index right bracket

    }

    protected final void appendTimeTypeScale(final FieldMeta<?> field, final StringBuilder builder) {
        final int fieldScale;
        switch ((fieldScale = field.scale())) {
            case -1:
                break;
            case 0:
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
                builder.append(_Constant.LEFT_PAREN)
                        .append(fieldScale)
                        .append(_Constant.RIGHT_PAREN);
                break;
            default:
                this.errorMsgList.add(String.format("%s scale[%s] error.", field, fieldScale));

        }// switch

    }


    protected <T> void appendIndexOutTableDef(final IndexMeta<T> index, final StringBuilder builder) {

    }


    protected final void precision(final FieldMeta<?> field, SqlType type
            , final long max, final long defaultValue, final StringBuilder builder) {
        final int precision = field.precision();
        if (precision > -1) {
            if (precision > max) {
                String m;
                m = String.format("%s precision[%s] out of [1,%s] error for %s.%s"
                        , field, field.scale(), max, type.getClass().getSimpleName(), type.name());
                this.errorMsgList.add(m);
                return;
            }
            builder.append(_Constant.LEFT_PAREN)
                    .append(precision)
                    .append(_Constant.RIGHT_PAREN);
        } else {
            builder.append(_Constant.LEFT_PAREN)
                    .append(defaultValue)
                    .append(_Constant.RIGHT_PAREN);
        }

    }

    protected final void noSpecifiedPrecision(FieldMeta<?> field) {
        this.errorMsgList.add(String.format("%s no precision.", field));
    }

    protected final void timeTypeScale(final FieldMeta<?> field, SqlType type, final StringBuilder builder) {
        final int scale = field.scale();
        if (scale > -1) {
            if (scale > 6) {
                timeScaleError(field, type);
                return;
            }
            builder.append(_Constant.LEFT_PAREN)
                    .append(scale)
                    .append(_Constant.RIGHT_PAREN);
        }
    }


    /**
     * @return true : complete
     */
    protected final boolean checkDefaultComplete(final FieldMeta<?> field, final String value) {
        final char[] array = value.toCharArray();
        final char identifierQuote = this.parser.identifierQuote;
        boolean quote = false, idQuote = false;
        LinkedList<Boolean> stack = null;
        char ch;
        for (int i = 0, last = array.length - 1; i < array.length; i++) {
            ch = array[i];
            if (quote) {
                if (ch == _Constant.BACK_SLASH) {
                    i++;
                    continue;
                } else if (ch != _Constant.QUOTE) {
                    continue;
                } else if (i < last && array[i + 1] == _Constant.QUOTE) {
                    i++;
                    continue;
                }
                quote = false;
            } else if (idQuote) {
                idQuote = false;
            } else if (ch == _Constant.QUOTE) {
                quote = true;
            } else if (ch == identifierQuote) {
                idQuote = true;
            } else if (ch == _Constant.LEFT_PAREN) {
                if (stack == null) {
                    stack = new LinkedList<>();
                }
                stack.push(Boolean.TRUE);
            } else if (ch == _Constant.RIGHT_PAREN) {
                if (stack == null || stack.size() == 0) {
                    // error
                    this.errorMsgList.add(String.format("%s default value ')' not match.", field));
                    break;
                }
                stack.pop();
            }


        }//for

        final boolean complete;
        if (quote) {
            complete = false;
            this.errorMsgList.add(String.format("%s default value ''' not close.", field));
        } else if (idQuote) {
            complete = false;
            String m = String.format("%s default value '%s' not close.", field, this.parser.identifierQuote);
            this.errorMsgList.add(m);
        } else if (stack != null && stack.size() > 0) {
            complete = false;
            String m = String.format("%s default value '%s' not close.", field, _Constant.LEFT_PAREN);
            this.errorMsgList.add(m);
        } else {
            complete = true;
        }

        return complete;
    }


    private void timeScaleError(FieldMeta<?> field, SqlType sqlType) {
        String m;
        m = String.format("%s scale[%s] error for %s.%s"
                , field, field.scale(), sqlType.getClass().getSimpleName(), sqlType.name());
        this.errorMsgList.add(m);

    }


    protected static void decimalType(final FieldMeta<?> field, final StringBuilder builder) {
        final int precision = field.precision();
        if (precision > 0) {
            builder.append(_Constant.LEFT_PAREN)
                    .append(field.precision());
            final int scale = field.scale();
            if (scale > -1) {
                builder.append(_Constant.COMMA)
                        .append(scale);
            }
            builder.append(_Constant.RIGHT_PAREN);
        }

    }


    /**
     * @see #createTable(TableMeta, List)
     */
    private <T> void appendOuterComment(final TableMeta<T> table, final List<String> sqlList) {
        StringBuilder commentBuilder;

        commentBuilder = new StringBuilder();
        this.appendComment(table, commentBuilder);
        sqlList.add(commentBuilder.toString());

        for (FieldMeta<T> field : table.fieldList()) {
            commentBuilder = new StringBuilder(30);
            this.appendComment(field, commentBuilder);
            sqlList.add(commentBuilder.toString());
        }


    }

    private <T> void doAppendIndexInTableDef(final TableMeta<T> table, final StringBuilder builder) {
        final ArmyParser parser = this.parser;
        for (IndexMeta<T> index : table.indexList()) {

            switch (parser.database) {
                case Postgre: {
                    if (!index.isPrimaryKey()) {
                        continue;
                    }
                }
                break;
                case H2:
                case MySQL:
                case Oracle:
                default://no-op
            }
            appendIndexInTableDef(index, builder);

        }

    }

    private <T> void appendIndexAfterTableDef(final TableMeta<T> table, final List<String> sqlList) {
        final ArmyParser parser = this.parser;
        StringBuilder builder;
        for (IndexMeta<T> index : table.indexList()) {

            switch (parser.database) {
                case Postgre: {
                    if (index.isPrimaryKey()) {
                        continue;
                    }
                }
                break;
                case H2:
                case MySQL:
                case Oracle:
                default:
                    continue;
            }
            builder = new StringBuilder(30);
            appendIndexOutTableDef(index, builder);
            sqlList.add(builder.toString());

        }
    }


}
