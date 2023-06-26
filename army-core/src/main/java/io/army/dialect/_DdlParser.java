package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.mapping.NoCastTextType;
import io.army.meta.*;
import io.army.schema._FieldResult;
import io.army.sqltype.SqlType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class _DdlParser<P extends _ArmyDialectParser> implements DdlParser {

    /**
     * non-static
     */
    protected final String SPACE_UNSIGNED = " UNSIGNED";

    /**
     * non-static
     */
    protected final String SPACE_COMMENT = " COMMENT";

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

    /**
     * non-static
     */
    protected final String ALTER_COLUMN_SPACE = "ALTER COLUMN ";

    /**
     * non-static
     */
    protected final String SPACE_DROP_DEFAULT = " DROP DEFAULT";

    /**
     * non-static
     */
    protected final String SPACE_SET_DEFAULT_SPACE = " SET DEFAULT ";


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
    public final void dropTable(List<TableMeta<?>> tableList, final List<String> sqlList) {
        final int size = tableList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(30);

        for (TableMeta<?> tableMeta : tableList) {
            builder.append("DROP TABLE IF EXISTS ");
            this.parser.safeObjectName(tableMeta, builder);
            sqlList.add(builder.toString());

            builder.setLength(0);  // clear
        }

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
            case PostgreSQL: {
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
        FieldMeta<?> field;
        for (int i = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (i == 0) {
                table = field.tableMeta();
                this.parser.safeObjectName(table, builder);
            } else if (field.tableMeta() != table) {
                throw new IllegalArgumentException("fieldList error");
            } else {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append("\n\tADD COLUMN");
            this.columnDefinition(field, builder);
        }

        sqlList.add(builder.toString());
    }


    @Override
    public final void modifyColumn(final List<_FieldResult> resultList, final List<String> sqlList) {
        final int size = resultList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(128)
                .append("ALTER TABLE ");

        switch (this.parser.database) {
            case PostgreSQL:
                builder.append("IF EXISTS ");
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
        }
        TableMeta<?> table = null;
        FieldMeta<?> field;
        _FieldResult result;

        List<FieldMeta<?>> commentFieldList = null;
        for (int i = 0; i < size; i++) {
            result = resultList.get(i);
            field = result.field();
            if (i == 0) {
                table = field.tableMeta();
                this.parser.safeObjectName(table, builder);
                //   .append("\n\t");
            } else if (field.tableMeta() != table) {
                throw new IllegalArgumentException("resultList error.");
            } else {
                builder.append(_Constant.SPACE_COMMA);
            }
            doModifyColumn(result, builder);

            switch (this.parser.database) {
                case PostgreSQL: {
                    if (result.containComment()) {
                        if (commentFieldList == null) {
                            commentFieldList = _Collections.arrayList();
                        }
                        commentFieldList.add(field);
                    }
                }
                break;
                case MySQL:
                case Oracle:
                case H2:
                default:
            }

        }//for

        sqlList.add(builder.toString()); // end


        if (commentFieldList != null) {
            for (FieldMeta<?> f : commentFieldList) {
                builder.setLength(0); // firstly,clear
                appendOuterComment(f, builder);
                sqlList.add(builder.toString());

            }
        }

    }


    @Override
    public final <T> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {
        final int indexNameSize = indexNameList.size();
        if (indexNameSize == 0) {
            return;
        }

        final List<IndexMeta<T>> indexMetaList = table.indexList();

        final Set<String> indexNameSet = new HashSet<>();
        IndexMeta<T> indexMeta;
        final StringBuilder builder = new StringBuilder(40);
        for (int i = 0; i < indexNameSize; i++) {
            final String indexName = indexNameList.get(i);
            indexMeta = null;
            for (IndexMeta<T> index : indexMetaList) {
                if (!index.name().equals(indexName)) {
                    continue;
                }
                if (indexNameSet.contains(indexName)) {
                    String m = String.format("Index[%s] duplication for %s", indexName, table);
                    throw new IllegalArgumentException(m);
                }
                indexMeta = index;
                indexNameSet.add(indexName);
                break;
            }
            if (indexMeta == null) {
                String m = String.format("Index[%s] not found in %s", indexName, table);
                throw new IllegalArgumentException(m);
            }

            appendIndexOutTableDef(indexMeta, builder);
            sqlList.add(builder.toString());

            builder.setLength(0); // clear

        }

    }

    @Override
    public final <T> void changeIndex(final TableMeta<T> table, final List<String> indexNameList, final List<String> sqlList) {
        dropIndex(table, indexNameList, sqlList);
        createIndex(table, indexNameList, sqlList);
    }

    @Override
    public final <T> void dropIndex(final TableMeta<T> table, final List<String> indexNameList,
                                    final List<String> sqlList) {
        final int indexNameSize = indexNameList.size();
        if (indexNameSize == 0) {
            return;
        }
        final boolean ifExists;
        switch (this.parser.database) {
            case PostgreSQL:
                ifExists = true;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                ifExists = false;
        }

        final StringBuilder builder = new StringBuilder(30);
        IndexMeta<T> index;
        for (final String indexName : indexNameList) {
            index = null;
            for (IndexMeta<T> indexMeta : table.indexList()) {
                if (indexMeta.name().equals(indexName)) {
                    index = indexMeta;
                    break;
                }
            }
            if (index == null) {
                String m = String.format("Not found index[%s] in %s.", indexName, table);
                throw new IllegalArgumentException(m);
            }
            builder.append("DROP INDEX ");
            if (ifExists) {
                builder.append("IF EXISTS ");
            }
            this.parser.identifier(index.name(), builder);
            sqlList.add(builder.toString());

            builder.setLength(0); // clear

        }// for

    }


    protected final void columnDefinition(final FieldMeta<?> field, final StringBuilder builder) {
        final int length;
        if ((length = builder.length()) > 0 && !Character.isWhitespace(builder.charAt(length - 1))) {
            builder.append(_Constant.SPACE);
        }
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
                appendOuterComment(field, builder);
                break;
            case H2:
            case Oracle:
            case PostgreSQL:
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.parser.database);

        }

    }

    protected final void defaultStartWithWhiteSpace(FieldMeta<?> field) {
        this.errorMsgList.add(String.format("%s start with white space.", field));
    }

    protected final void appendSpaceIfNeed(StringBuilder builder) {
        final int length;
        if ((length = builder.length()) > 0 && !Character.isWhitespace(builder.charAt(length - 1))) {
            builder.append(_Constant.SPACE);
        }
    }


    protected abstract void dataType(FieldMeta<?> field, SqlType type, StringBuilder builder);

    protected abstract void postDataType(FieldMeta<?> field, SqlType type, StringBuilder builder);

    protected abstract void appendTableOption(final TableMeta<?> table, final StringBuilder builder);

    protected abstract void appendPostGenerator(final FieldMeta<?> field, final StringBuilder builder);

    protected void appendOuterComment(final DatabaseObject object, final StringBuilder builder) {

        builder.append(SPACE_COMMENT)
                .append(_Constant.SPACE);
        this.parser.literal(NoCastTextType.INSTANCE, object.comment(), builder);
    }


    protected abstract void doModifyColumn(_FieldResult result, StringBuilder builder);


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
            case PostgreSQL:
            case Oracle:
            case H2:
                break;

            default:// no-op
        }

        switch (this.parser.database) {
            case MySQL:
                appendIndexType(index, builder);
                break;
            case PostgreSQL:
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


    /**
     * @param builder length is zero
     */
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
        this.appendOuterComment(table, commentBuilder);
        sqlList.add(commentBuilder.toString());

        for (FieldMeta<T> field : table.fieldList()) {
            commentBuilder = new StringBuilder(30);
            this.appendOuterComment(field, commentBuilder);
            sqlList.add(commentBuilder.toString());
        }


    }

    private <T> void doAppendIndexInTableDef(final TableMeta<T> table, final StringBuilder builder) {
        final ArmyParser parser = this.parser;
        for (IndexMeta<T> index : table.indexList()) {

            switch (parser.database) {
                case PostgreSQL: {
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
        final StringBuilder builder = new StringBuilder(30);
        for (IndexMeta<T> index : table.indexList()) {

            switch (parser.database) {
                case PostgreSQL: {
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
            appendIndexOutTableDef(index, builder);
            sqlList.add(builder.toString());

            builder.setLength(0); // clear

        }//for


    }


}
