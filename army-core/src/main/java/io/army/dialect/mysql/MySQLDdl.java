package io.army.dialect.mysql;

import io.army.dialect._AbstractDialectParser;
import io.army.dialect._Constant;
import io.army.dialect._DdlDialect;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;
import io.army.struct.TextEnum;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class MySQLDdl extends _DdlDialect {

    static MySQLDdl create(_AbstractDialectParser dialect) {
        return new MySQLDdl(dialect);
    }

    MySQLDdl(_AbstractDialectParser dialect) {
        super(dialect);
    }

    @Override
    public void modifyTableComment(final TableMeta<?> table, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder()
                .append("ALTER TABLE ");
        this.dialect.safeObjectName(table, builder);
        appendComment(table.comment(), builder);

    }

    @Override
    public void modifyColumn(final List<_FieldResult> resultList, final List<String> sqlList) {
        final int size = resultList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(128)
                .append("ALTER TABLE ");
        final _AbstractDialectParser dialect = this.dialect;
        TableMeta<?> table = null;
        FieldMeta<?> field;
        _FieldResult result;

        for (int i = 0; i < size; i++) {
            result = resultList.get(i);
            field = result.field();
            if (i > 0) {
                if (field.tableMeta() != table) {
                    throw new IllegalArgumentException("resultList error.");
                }
                builder.append(" ,\n\t");
            } else {
                table = field.tableMeta();
                dialect.identifier(table.tableName(), builder)
                        .append("\n\t");
            }

            if (result.comment() || result.sqlType() || result.nullable()) {
                builder.append("CHANGE COLUMN ");
                dialect.identifier(field.columnName(), builder)
                        .append(_Constant.SPACE);
                columnDefinition(field, builder);
                continue;
            }
            final String defaultValue;
            defaultValue = field.defaultValue();
            builder.append("ALTER COLUMN ");
            dialect.identifier(field.columnName(), builder);
            if (defaultValue.length() == 0) {
                builder.append(" DROP DEFAULT");
            } else if (Character.isWhitespace(defaultValue.charAt(0))) {
                defaultStartWithWhiteSpace(field);
                return;
            } else if (checkDefaultComplete(field, defaultValue)) {
                builder.append(" SET DEFAULT ")
                        .append(defaultValue);
            }//no else,checkDefaultComplete method have handled.

        }//for

        sqlList.add(builder.toString());
    }


    @Override
    public <T> void createIndex(final TableMeta<T> table, final List<String> indexNameList
            , final List<String> sqlList) {
        final int indexNameSize = indexNameList.size();
        if (indexNameSize == 0) {
            throw new IllegalArgumentException("indexNameList must not empty.");
        }
        if (table.javaType().getName().equals("io.army.example.domain.ChinaProvince")) {
            for (String s : indexNameList) {
                System.out.println(s);
            }
        }
        final StringBuilder builder = new StringBuilder(128)
                .append("ALTER TABLE ");

        final _AbstractDialectParser dialect = this.dialect;

        dialect.identifier(table.tableName(), builder)
                .append("\n\t");

        final List<IndexMeta<T>> indexMetaList = table.indexList();

        final Set<String> indexNameSet = new HashSet<>();
        for (int i = 0; i < indexNameSize; i++) {
            final String indexName = indexNameList.get(i);
            IndexMeta<T> indexMeta = null;
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
            if (i > 0) {
                builder.append(" ,\n\t");
            }
            if (indexMeta.unique()) {
                builder.append("ADD UNIQUE INDEX ");
            } else {
                builder.append("ADD INDEX ");
            }
            dialect.identifier(indexMeta.name(), builder);

            final String indexType = indexMeta.type();
            if (_StringUtils.hasText(indexType)) {
                builder.append(" USING ");
                dialect.identifier(indexType, builder);
            }
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<IndexFieldMeta<T>> indexFieldList = indexMeta.fieldList();
            final int fieldSize = indexFieldList.size();
            for (int j = 0; j < fieldSize; j++) {
                if (j > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(_Constant.SPACE);
                dialect.identifier(indexFieldList.get(j).columnName(), builder);
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);


        }

        sqlList.add(builder.toString());

    }


    @Override
    public <T> void changeIndex(final TableMeta<T> table, final List<String> indexNameList
            , final List<String> sqlList) {

        dropIndex(table, indexNameList, sqlList);
        createIndex(table, indexNameList, sqlList);
    }

    @Override
    public <T> void dropIndex(TableMeta<T> table, List<String> indexNameList
            , List<String> sqlList) {
        final int indexNameSize = indexNameList.size();
        if (indexNameSize == 0) {
            throw new IllegalArgumentException("indexNameList must not empty.");
        }
        final StringBuilder builder = new StringBuilder(128)
                .append("ALTER TABLE ");
        final _AbstractDialectParser dialect = this.dialect;
        dialect.identifier(table.tableName(), builder)
                .append("\n\t");
        for (int i = 0; i < indexNameSize; i++) {
            if (i > 0) {
                builder.append(" ,\n\t");
            }
            final String indexName = indexNameList.get(i);

            IndexMeta<T> index = null;
            for (IndexMeta<T> indexMeta : table.indexList()) {
                if (indexMeta.name().equals(indexName)) {
                    index = indexMeta;
                    break;
                }
            }
            if (index == null) {
                String m = String.format("Not found index[%s] in %s.", indexName, index);
                throw new IllegalArgumentException(m);
            }
            builder.append("DROP INDEX ");
            dialect.identifier(indexName, builder);

        }
        sqlList.add(builder.toString());

    }

    @Override
    protected void dataType(final FieldMeta<?> field, final SqlType type, final StringBuilder builder) {
        switch ((MySQLTypes) type) {
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
                if (type == MySQLTypes.DECIMAL_UNSIGNED) {
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
                throw _Exceptions.unexpectedEnum((MySQLTypes) type);
        }


    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        builder.append(" AUTO_INCREMENT");
    }

    @Override
    protected void appendTableOption(final TableMeta<?> table, final StringBuilder builder) {
        builder.append(" ENGINE = InnoDB CHARACTER SET  = 'utf8mb4'");
        appendComment(table.comment(), builder);
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
