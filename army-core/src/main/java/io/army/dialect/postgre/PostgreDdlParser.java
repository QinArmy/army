package io.army.dialect.postgre;

import io.army.annotation.GeneratorType;
import io.army.dialect._Constant;
import io.army.dialect._DdlParser;
import io.army.mapping.NoCastTextType;
import io.army.meta.DatabaseObject;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

final class PostgreDdlParser extends _DdlParser<PostgreParser> {

    static PostgreDdlParser create(PostgreParser dialect) {
        return new PostgreDdlParser(dialect);
    }

    private PostgreDdlParser(PostgreParser dialect) {
        super(dialect);
    }

    @Override
    public void modifyTableComment(TableMeta<?> table, List<String> sqlList) {

    }

    @Override
    public void modifyColumn(List<_FieldResult> resultList, List<String> sqlList) {

    }

    @Override
    public <T> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    public <T> void changeIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    public <T> void dropIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    protected void dataType(final FieldMeta<?> field, final SqlType type, final StringBuilder builder) {
        switch ((PostgreSqlType) type) {
            case UNKNOWN:
                throw _Exceptions.unexpectedEnum((Enum<?>) type);
            case DECIMAL:
            case DECIMAL_ARRAY:
                this.appendDecimalDateType(field, type, builder);
                break;
            case TIME:
            case TIMETZ:
            case TIMESTAMP:
            case TIMESTAMPTZ:
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY:
                this.appendTimeDateType(field, type, builder);
                break;
            default:
                this.parser.typeName(field.mappingType(), builder);


        }// switch

    }


    @Override
    protected void postDataType(FieldMeta<?> field, SqlType type, StringBuilder builder) {
        switch ((PostgreSqlType) type) {
            case SMALLINT:
                builder.append("SMALLSERIAL");
                break;
            case INTEGER:
                builder.append("SERIAL");
                break;
            case BIGINT:
                builder.append("BIGSERIAL");
                break;
            default:
                this.errorMsgList.add(String.format("%s %s don't support %s", field, type, GeneratorType.POST));
        }

    }

    @Override
    protected void appendComment(final DatabaseObject object, final StringBuilder builder) {
        builder.append(SPACE_COMMENT)
                .append(_Constant.SPACE_ON);

        if (object instanceof TableMeta) {
            builder.append(" TABLE ");
            this.parser.safeObjectName(object, builder);
        } else if (object instanceof FieldMeta) {
            builder.append(" COLUMN ");
            this.parser.safeObjectName(((FieldMeta<?>) object).tableMeta(), builder);
            builder.append(_Constant.POINT);
            this.parser.safeObjectName(object, builder);
        } else {
            //no bug,never here
            throw new IllegalArgumentException();
        }

        builder.append(" IS ");

        this.parser.literal(NoCastTextType.INSTANCE, object.comment(), builder);

    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        //no-op,see this.dataType() method
    }

    @Override
    protected void appendTableOption(TableMeta<?> table, StringBuilder builder) {
        //no-op
    }


    @Override
    protected <T> void appendIndexOutTableDef(final IndexMeta<T> index, final StringBuilder builder) {
        if (index.isPrimaryKey()) {
            // no bug,never here
            throw new IllegalArgumentException("error ,is primary key");
        }
        if (builder.length() > 0) {
            builder.append(_Constant.SPACE);
        }
        builder.append("CREATE");

        if (index.isUnique()) {
            builder.append(" UNIQUE");
        }

        builder.append(" INDEX IF NOT EXISTS ");
        this.parser.identifier(index.name(), builder);
        builder.append(_Constant.SPACE_ON_SPACE);
        this.parser.safeObjectName(index.table(), builder);

        final String type;
        if (_StringUtils.hasText((type = index.type()))) {
            builder.append(" USING ");
            this.parser.identifier(type, builder);
        }

        appendIndexFieldList(index, builder);

    }


    /**
     * @see #dataType(FieldMeta, SqlType, StringBuilder)
     */
    private void appendTimeDateType(final FieldMeta<?> field, final SqlType type, final StringBuilder builder) {
        String safeTypeName;
        safeTypeName = type.name();
        final int index;
        if ((index = safeTypeName.lastIndexOf("_ARRAY")) > 0) {
            safeTypeName = safeTypeName.substring(0, index);
        }
        builder.append(safeTypeName);

        appendTimeTypeScale(field, builder);

        if (index > 0) {
            this.parser.arrayTypeName(safeTypeName, ArrayUtils.dimensionOfType(field.mappingType()), builder);
        }
    }

    /**
     * @see #dataType(FieldMeta, SqlType, StringBuilder)
     */
    private void appendDecimalDateType(final FieldMeta<?> field, final SqlType type, final StringBuilder builder) {
        int precision, scale;
        precision = field.precision();
        scale = field.scale();
        if (precision < scale) {
            this.errorMsgList.add(String.format("%s precision[%s] scale[%s]", field, precision, scale));
            return;
        }
        if (precision == -1) {
            precision = 14;
        }
        if (scale == -1) {
            scale = 2;
        }
        String safeTypeName;
        safeTypeName = type.name();
        final int index;
        if ((index = safeTypeName.lastIndexOf("_ARRAY")) > 0) {
            safeTypeName = safeTypeName.substring(0, index);
        }

        builder.append(safeTypeName)
                .append(_Constant.LEFT_PAREN)
                .append(precision)
                .append(_Constant.COMMA)
                .append(scale)
                .append(_Constant.RIGHT_PAREN);

        if (index > 0) {
            this.parser.arrayTypeName(safeTypeName, ArrayUtils.dimensionOfType(field.mappingType()), builder);
        }

    }


}
