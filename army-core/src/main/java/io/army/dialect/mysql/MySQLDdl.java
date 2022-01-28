package io.army.dialect.mysql;

import io.army.dialect.Constant;
import io.army.dialect._AbstractDialect;
import io.army.dialect._DdlDialect;
import io.army.meta.FieldMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;

import java.time.DateTimeException;

abstract class MySQLDdl extends _DdlDialect {

    private MySQLDdl(_AbstractDialect dialect) {
        super(dialect);
    }


    @Override
    protected final void dataType(final FieldMeta<?, ?> field, final SqlType type, final StringBuilder builder) {
        switch ((MySqlType) type) {
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
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                builder.append(type.name());
                break;
            case DECIMAL: {
                builder.append(type.name());
                decimalType(field, builder);
            }
            break;
            case TIME:
            case DATETIME: {
                builder.append(type.name());
                timeTypeScale(field, type, builder);
            }
            break;
            case TINYTEXT:
            case TINYBLOB:
            case CHAR: {
                builder.append(type.name());
                precision(field, type, (1 << 8) - 1, builder);
            }
            break;
            case VARBINARY:
            case VARCHAR: {
                builder.append(type.name());
                precision(field, type, 21844, builder);
            }
            break;
            case SET:
                setType(field, builder);
                break;
            case BIT: {
                builder.append(type.name());
                precision(field, type, 64, builder);
            }
            break;
            case ENUM:
                enumType(field, builder);
                break;
            case BLOB:
            case BINARY:
            case TEXT: {
                builder.append(type.name());
                precision(field, type, (1 << 16) - 1, builder);
            }
            break;
            case MEDIUMBLOB:
            case MEDIUMTEXT: {
                builder.append(type.name());
                precision(field, type, (1 << 24) - 1, builder);
            }
            break;
            case LONGBLOB:
            case LONGTEXT: {
                builder.append(type.name());
                if (field.precision() == Integer.MIN_VALUE) {
                    builder.append(Constant.LEFT_BRACKET)
                            .append((1L << 32) - 1)
                            .append(Constant.RIGHT_BRACKET);
                } else {
                    precision(field, type, Integer.MAX_VALUE, builder);
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
            case DECIMAL_UNSIGNED: {
                builder.append("DECIMAL UNSIGNED");
                decimalType(field, builder);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) type);
        }


    }


    @Override
    protected final void defaultValue(final FieldMeta<?, ?> field, final SqlType type, final StringBuilder builder) {
        switch ((MySqlType) type) {
            case INT:
                appendIntegerDefault(field, type, Integer.MIN_VALUE, Integer.MAX_VALUE, builder);
                break;
            case BIGINT:
                appendIntegerDefault(field, type, Long.MIN_VALUE, Long.MAX_VALUE, builder);
                break;
            case DECIMAL:
                appendDecimalDefault(field, type, false, builder);
                break;
            case BOOLEAN:
                appendBooleanDefault(field, builder);
                break;
            case DATETIME:
                appendDatetimeDefault(field, builder);
                break;
            case DATE:
                appendDateDefault(field, builder);
                break;
            case TIME:
                appendTimeDefault(field, builder);
            case YEAR:
                appendYearDefault(field, type, builder);
                break;
            case CHAR:
            case VARCHAR:
            case JSON:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
                appendTextDefault(field, type, builder);
                break;
            case ENUM:
            case SET:
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:

            case BIT:
            case FLOAT:
            case DOUBLE:

            case TINYINT:
            case TINYINT_UNSIGNED:
            case SMALLINT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT_UNSIGNED:
            case BIGINT_UNSIGNED:
            case DECIMAL_UNSIGNED:

            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) type);
        }
    }


    private static void enumType(final FieldMeta<?, ?> field, final StringBuilder builder) {
        builder.append("ENUM(");
        int index = 0;
        for (Object e : field.javaType().getEnumConstants()) {
            if (index > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            builder.append(((Enum<?>) e).name());
            index++;
        }
        builder.append(Constant.SPACE_RIGHT_BRACKET);
    }

    private static void setType(final FieldMeta<?, ?> field, final StringBuilder builder) {
        builder.append("SET(");
        int index = 0;
        for (Object e : field.elementType().getEnumConstants()) {
            if (index > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            builder.append(Constant.SPACE)
                    .append(((Enum<?>) e).name());
            index++;
        }
        builder.append(Constant.SPACE_RIGHT_BRACKET);
    }

    private void appendYearDefault(final FieldMeta<?, ?> field, final SqlType type, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (!checkQuoteValue(field, defaultValue)) {
            try {
                final int value;
                value = Integer.parseInt(defaultValue);
                if (value < 1901 || value > 2155) {
                    defaultValueOutOfNumberRange(field, type, 1901, 2155);
                    return;
                }
            } catch (DateTimeException e) {
                //ignore not literal value.
            }
        }
        builder.append(defaultValue);
    }


}
