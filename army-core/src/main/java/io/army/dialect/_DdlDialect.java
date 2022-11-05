package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.meta.*;
import io.army.sqltype.SqlType;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class _DdlDialect implements DdlDialect {

    protected static final String SPACE_UNSIGNED = " UNSIGNED";

    protected final List<String> errorMsgList = new ArrayList<>();

    protected final ArmyParser dialect;

    protected final ServerMeta serverMeta;

    protected _DdlDialect(ArmyParser dialect) {
        this.dialect = dialect;
        this.serverMeta = dialect.serverMeta;
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
        final ArmyParser dialect = this.dialect;
        final StringBuilder builder = new StringBuilder(size * 10);
        builder.append("DROP TABLE IF EXISTS ");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            }
            dialect.identifier(tableList.get(i).tableName(), builder);
        }
        sqlList.add(builder.toString());
    }

    @Override
    public final <T> void createTable(final TableMeta<T> table, List<String> sqlList) {
        final ArmyParser dialect = this.dialect;
        final StringBuilder builder = new StringBuilder(128)
                .append("CREATE TABLE IF NOT EXISTS ");

        dialect.safeObjectName(table, builder)
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
        this.index(table, builder);
        builder.append('\n')
                .append(_Constant.SPACE_RIGHT_PAREN);

        appendTableOption(table, builder);
        sqlList.add(builder.toString());
    }

    @Override
    public final void addColumn(final List<FieldMeta<?>> fieldList, final List<String> sqlList) {
        final int fieldSize = fieldList.size();
        if (fieldSize == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(128)
                .append("ALTER TABLE ");
        final ArmyParser dialect = this.dialect;

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
                dialect.safeObjectName(table, builder)
                        .append(" ADD COLUMN (\n\t");
            }
            //TODO 新增的 时间类型列应该有默认值,否则 mysql 会以 00000-00-00 作为默认值.
            this.columnDefinition(field, builder);
        }
        builder.append("\n)");
        sqlList.add(builder.toString());
    }


    protected final void columnDefinition(final FieldMeta<?> field, final StringBuilder builder) {
        this.dialect.safeObjectName(field, builder)
                .append(_Constant.SPACE);
        final SqlType sqlType;
        sqlType = field.mappingType().map(this.serverMeta);

        this.dataType(field, sqlType, builder);

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
        appendComment(field.comment(), builder);

    }

    protected final void defaultStartWithWhiteSpace(FieldMeta<?> field) {
        this.errorMsgList.add(String.format("%s start with white space.", field));
    }


    protected abstract void dataType(FieldMeta<?> field, SqlType type, StringBuilder builder);

    protected abstract void appendTableOption(final TableMeta<?> table, final StringBuilder builder);

    protected abstract void appendPostGenerator(final FieldMeta<?> field, final StringBuilder builder);

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


    protected final void appendComment(final String comment, final StringBuilder builder) {
        final char[] array = comment.toCharArray();
        builder.append(" COMMENT '");
        int lastWritten = 0;
        for (int i = 0; i < array.length; i++) {
            switch (array[i]) {
                case _Constant.QUOTE: {
                    if (i > lastWritten) {
                        builder.append(array, lastWritten, i - lastWritten);
                    }
                    builder.append(_Constant.QUOTE);
                    lastWritten = i; // not i+1 as ch wasn't written.
                }
                break;
                case _Constant.BACK_SLASH: {
                    if (i > lastWritten) {
                        builder.append(array, lastWritten, i - lastWritten);
                    }
                    builder.append(_Constant.BACK_SLASH);
                    lastWritten = i; // not i+1 as ch wasn't written.
                }
                break;
                case _Constant.EMPTY_CHAR: {
                    if (i > lastWritten) {
                        builder.append(array, lastWritten, i - lastWritten);
                    }
                    builder.append(_Constant.BACK_SLASH)
                            .append('0');
                    lastWritten = i + 1; //  i+1
                }
                break;
                case '\032': {
                    if (i > lastWritten) {
                        builder.append(array, lastWritten, i - lastWritten);
                    }
                    builder.append(_Constant.BACK_SLASH)
                            .append('Z');
                    lastWritten = i + 1; //  i+1
                }
                break;
                default://no-op
            }
        }

        if (lastWritten < array.length) {
            builder.append(array, lastWritten, array.length - lastWritten);
        }
        builder.append(_Constant.QUOTE);
    }

    /**
     * @return true : complete
     */
    protected final boolean checkDefaultComplete(final FieldMeta<?> field, final String value) {
        final char[] array = value.toCharArray();
        final char identifierQuote = this.dialect.identifierQuote;
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
            String m = String.format("%s default value '%s' not close.", field, this.dialect.identifierQuote);
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


    private <T> void index(final TableMeta<T> table, final StringBuilder builder) {
        final ArmyParser dialect = this.dialect;
        for (IndexMeta<T> index : table.indexList()) {


            if (index.isPrimaryKey()) {
                final String indexName = index.name();
                if (indexName.isEmpty()) {
                    builder.append(" ,\n\tPRIMARY KEY");
                } else {
                    builder.append(" ,\n\tCONSTRAINT ");
                    dialect.identifier(indexName, builder);
                }
            } else if (index.unique()) {
                builder.append(" ,\n\tUNIQUE ");
                dialect.identifier(index.name(), builder);
            } else {
                builder.append(" ,\n\tINDEX ");
                dialect.identifier(index.name(), builder);
            }
            final String type;
            type = index.type();
            if (_StringUtils.hasText(type)) {
                builder.append(" USING ");
                dialect.identifier(index.type(), builder);
            }
            builder.append(_Constant.SPACE_LEFT_PAREN);// index left bracket

            final List<IndexFieldMeta<T>> indexFieldList = index.fieldList();
            final int indexFieldSize = indexFieldList.size();
            for (int i = 0; i < indexFieldSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                final IndexFieldMeta<T> indexField;
                indexField = indexFieldList.get(i);

                builder.append(_Constant.SPACE);
                dialect.identifier(indexField.columnName(), builder);

                final Boolean asc = indexField.fieldAsc();
                if (asc != null) {
                    if (asc) {
                        builder.append(_Constant.SPACE_ASC);
                    } else {
                        builder.append(_Constant.SPACE_DESC);
                    }
                }
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN); // index right bracket
        }

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


}
