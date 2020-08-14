package io.army.sqldatatype;

import io.army.dialect.DDLUtils;
import io.army.dialect.Database;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;

import java.time.*;
import java.util.Map;

public enum MySQLDataType implements SQLDataType {

    BIT {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) {
            int precision = fieldMeta.precision();
            if (precision > 64) {
                throw SQLDataTypeUtils.createPrecisionException(this, 1, 64, fieldMeta);
            }
            SQLDataTypeUtils.appendDataTypeWithPrecision(this, precision, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("B'0'");
        }
    },
    TINYINT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    BOOLEAN {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("FALSE");
        }
    },

    SMALLINT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    MEDIUMINT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    INT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    BIGINT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    DECIMAL {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.decimalDataTypeClause(this, 65, 30, fieldMeta, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append(SQLDataTypeUtils.decimalDefaultValue(fieldMeta));
        }
    },

    FLOAT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    DOUBLE {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    DATE {
        @Override
        public boolean supportNowValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType != LocalDate.class && javaType != YearMonth.class && javaType != MonthDay.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            if (database.compatible(Database.MySQL80)) {
                builder.append(DDLUtils.zeroForTimeType(fieldMeta));
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalDate.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            if (database.compatible(Database.MySQL80)) {
                SQLDataTypeUtils.mySQLDateNowValue(fieldMeta, builder);
            } else {
                throw SQLDataTypeUtils.createNotSupportNowExpressionException(this, fieldMeta, database);
            }
        }
    },

    TIME {
        @Override
        public boolean supportNowValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            if (supportZeroValue(database)) {
                builder.append(DDLUtils.zeroForTimeType(fieldMeta));
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            if (supportNowValue(database)) {
                builder.append("(CURRENT_TIME)");
            } else {
                throw SQLDataTypeUtils.createNotSupportNowExpressionException(this, fieldMeta, database);
            }
        }
    },

    DATETIME {
        @Override
        public boolean supportNowValue(Database database) {
            return true;
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append(DDLUtils.zeroForTimeType(fieldMeta));
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            int precision = fieldMeta.precision();
            builder.append("CURRENT_TIMESTAMP");
            if (precision > 0 && precision < 7) {
                builder.append("(")
                        .append(precision)
                        .append(")");
            } else if (precision > 6) {
                throw new MetaException("%s, NOW/CURRENT_TIMESTAMP funcion precision must in [0,6]", fieldMeta);
            }
        }
    },

    YEAR {
        @Override
        public boolean supportNowValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != Year.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append(DDLUtils.zeroForTimeType(fieldMeta));
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportNowValue(database)) {
                builder.append("(YEAR(CURRENT_DATE))");
            } else {
                throw SQLDataTypeUtils.createNotSupportNowExpressionException(this, fieldMeta, database);
            }
        }
    },

    CHAR {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 8) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("''");
        }
    },

    NCHAR {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 8) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) {
            CHAR.zeroValue(fieldMeta, builder, database);
        }
    },

    VARCHAR {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 16) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) {
            builder.append("''");
        }
    },

    NVARCHAR {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 16) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) {
            builder.append("''");
        }
    },

    BINARY {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 8) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) {
            builder.append("0x00");
        }

    },

    VARBINARY {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 16) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0x00");
        }
    },

    TINYBLOB {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 8) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0x00");
        }
    },

    BLOB {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 16) - 1, 1024, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0x00");
        }
    },

    MEDIUMBLOB {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 24) - 1, 2048, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0x00");
        }
    },

    TINYTEXT {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 8) - 1, 255, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("''");
        }
    },

    TEXT {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 16) - 1, 1024, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) throws MetaException {
            builder.append("''");
        }
    },

    MEDIUMTEXT {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, (1 << 24) - 1, 2048, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("''");
        }
    },
    ENUM {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (!javaType.isEnum()) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("ENUM(");
            int index = 0;
            for (Object e : javaType.getEnumConstants()) {
                if (index > 0) {
                    builder.append(",");
                }
                builder.append("'")
                        .append(((Enum<?>) e).name())
                        .append("'");
                index++;
            }
            builder.append(")");
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType == Month.class) {
                builder.append("(UPPER(MONTHNAME(CURRENT_DATE)))");
            } else if (javaType == DayOfWeek.class) {
                builder.append("(UPPER(DATE_FORMAT(CURRENT_DATE,'%W')))");
            } else {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append(1);
        }
    },
    JSON {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'{}'");
        }
    },
    GEOMETRY {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    },
    POINT {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    },
    LINESTRING {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    },
    POLYGON {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    },
    MULTIPOINT {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    }, MULTILINESTRING {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    },
    MULTIPOLYGON {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    }, GEOMETRYCOLLECTION {
        @Override
        public boolean supportZeroValue(Database database) {
            return database.compatible(Database.MySQL80);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database) throws MetaException {
            if (supportZeroValue(database)) {
                //TODO
            } else {
                throw SQLDataTypeUtils.createNotSupportZeroValueException(this, fieldMeta, database);
            }
        }
    };


    @Override
    public final Database database() {
        return Database.MySQL;
    }


    public static final Map<String, MySQLDataType> TYPE_NAME_MAP = SQLDataTypeUtils.createTypeNameMap(MySQLDataType.class);

    public static boolean mySQLDataType(String typeName) {
        return TYPE_NAME_MAP.containsKey(typeName.toUpperCase());
    }

}
