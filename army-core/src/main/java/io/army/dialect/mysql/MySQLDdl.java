package io.army.dialect.mysql;

import io.army.dialect._Constant;
import io.army.dialect._DdlParser;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.struct.TextEnum;
import io.army.util._Exceptions;

import java.util.List;

final class MySQLDdl extends _DdlParser<MySQLParser> {

    static MySQLDdl create(MySQLParser dialect) {
        return new MySQLDdl(dialect);
    }

    MySQLDdl(MySQLParser dialect) {
        super(dialect);
    }

    @Override
    public void modifyTableComment(final TableMeta<?> table, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder()
                .append("ALTER TABLE ");
        this.parser.safeObjectName(table, builder);
        appendComment(table, builder);

    }


    @Override
    public <T> void changeIndex(final TableMeta<T> table, final List<String> indexNameList
            , final List<String> sqlList) {

        dropIndex(table, indexNameList, sqlList);
        createIndex(table, indexNameList, sqlList);
    }


    @Override
    protected <T> void appendIndexOutTableDef(final IndexMeta<T> index, final StringBuilder builder) {
        if (index.isPrimaryKey()) {
            throw new IllegalArgumentException();
        }
        if (builder.length() > 0) {
            builder.append(_Constant.SPACE);
        }
        builder.append("CREATE");
        if (index.isUnique()) {
            builder.append(" UNIQUE");
        }

        builder.append(" INDEX ");
        this.parser.identifier(index.name(), builder);
        appendIndexType(index, builder);

        builder.append(_Constant.SPACE_ON_SPACE);
        this.parser.safeObjectName(index.tableMeta(), builder);
        appendIndexFieldList(index, builder);

    }

    @Override
    protected void dataType(final FieldMeta<?> field, final SqlType type, final StringBuilder builder) {
        switch ((MySQLType) type) {
            case TINYINT:
            case SMALLINT:
            case INT:
            case MEDIUMINT:
            case BIGINT:
            case DATE:
            case YEAR:
            case BOOLEAN:
            case JSON:
            case FLOAT:
            case DOUBLE:

            case TINYTEXT:
            case TINYBLOB:
            case MEDIUMBLOB:
            case MEDIUMTEXT:
            case LONGBLOB:
            case LONGTEXT:

            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                builder.append(type.name());
                break;
            case DECIMAL_UNSIGNED:
            case DECIMAL: {
                builder.append("DECIMAL");
                decimalType(field, builder);
                if (type == MySQLType.DECIMAL_UNSIGNED) {
                    builder.append(SPACE_UNSIGNED);
                }
            }
            break;
            case TIME:
            case DATETIME: {
                builder.append(type.name());
                timeTypeScale(field, type, builder);
            }
            break;
            case BINARY:
            case CHAR: {
                builder.append(type.name());
                precision(field, type, 0xFF, 1, builder);
            }
            break;
            case VARBINARY:
            case VARCHAR: {
                builder.append(type.name());
                precision(field, type, 0xFFFF, 0xFF, builder);
            }
            break;
            case SET:
                setType(field, builder);
                break;
            case BIT: {
                builder.append(type.name());
                precision(field, type, 64, 1, builder);
            }
            break;
            case ENUM:
                enumType(field, builder);
                break;
            case BLOB:
            case TEXT: {
                builder.append(type.name());
                if (field.precision() > 0) {
                    precision(field, type, 0xFFFF, 0xFFFF, builder);
                }
            }
            break;
            case TINYINT_UNSIGNED:
                builder.append("TINYINT UNSIGNED");
                break;
            case SMALLINT_UNSIGNED:
                builder.append("SMALLINT UNSIGNED");
                break;
            case MEDIUMINT_UNSIGNED:
                builder.append("MEDIUMINT UNSIGNED");
                break;
            case INT_UNSIGNED:
                builder.append("INT UNSIGNED");
                break;
            case BIGINT_UNSIGNED:
                builder.append("BIGINT UNSIGNED");
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) type);
        }


    }

    @Override
    protected void postDataType(FieldMeta<?> field, SqlType type, StringBuilder builder) {
        // no-op
    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        builder.append(" AUTO_INCREMENT");
    }

    @Override
    protected void appendTableOption(final TableMeta<?> table, final StringBuilder builder) {
        builder.append(" ENGINE = InnoDB CHARACTER SET  = 'utf8mb4'");
        appendComment(table, builder);
    }

    private void setType(final FieldMeta<?> field, final StringBuilder builder) {
        builder.append("SET(");
        int index = 0;
        for (Object e : field.elementTypes().get(0).getEnumConstants()) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.QUOTE);
            if (e instanceof TextEnum) {
                builder.append(((TextEnum) e).text());
            } else {
                builder.append(((Enum<?>) e).name());
            }
            builder.append(_Constant.QUOTE);

            index++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }

    private static void enumType(final FieldMeta<?> field, final StringBuilder builder) {
        builder.append("ENUM(");
        int index = 0;
        for (Object e : field.javaType().getEnumConstants()) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.QUOTE);
            if (e instanceof TextEnum) {
                builder.append(((TextEnum) e).text());
            } else {
                builder.append(((Enum<?>) e).name());
            }
            builder.append(_Constant.QUOTE);
            index++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }


}
