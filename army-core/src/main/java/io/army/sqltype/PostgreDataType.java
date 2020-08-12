package io.army.sqltype;

import io.army.criteria.MetaException;
import io.army.dialect.DDLUtils;
import io.army.dialect.Database;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;

import java.time.*;

/**
 * @see <a href="https://www.postgresql.org/docs/11/datatype.html">Postgre Data Types</a>
 */
public enum PostgreDataType implements SQLDataType {

    /*################################## blow Numeric Types  ##################################*/

    SMALLINT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }

    },
    INTEGER {
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
            // up to 131072 digits before the decimal point; up to 16383 digits after the decimal point
            SQLDataTypeUtils.decimalDataTypeClause(this, 147455, 16383, fieldMeta, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            SQLDataTypeUtils.decimalDefaultValue(fieldMeta);
        }
    },
    REAL {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },
    DOUBLE_PRECISION {
        @Override
        public String typeName() {
            return "DOUBLE PRECISION";
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0");
        }
    },

    /*################################## blow money Types  ##################################*/
    MONEY {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("0.00");
        }
    },

    /*################################## blow Character Types  ##################################*/

    CHAR {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            // postgre 11 document not specified the maximum length
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, Integer.MAX_VALUE, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("''");
        }
    },
    VARCHAR {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            // postgre 11 document not specified the maximum length
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, Integer.MAX_VALUE, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("''");
        }
    },
    TEXT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("''");
        }
    },

    /*################################## blow  Binary Data Types  ##################################*/

    BYTEA {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'\\000'");
        }
    },

    /*################################## blow  Date/Time Data Types  ##################################*/

    TIME_WITHOUT_TIME_ZONE {

        @Override
        public boolean supportNowValue(Database database) {
            return true;
        }

        @Override
        public String typeName() {
            return "TIME WITHOUT TIME ZONE";
        }

        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            if (fieldMeta.javaType() != LocalTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("TIME(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(") WITHOUT TIME ZONE");
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("LOCALTIME(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(")");
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append(DDLUtils.zeroForTimeType(fieldMeta));
        }
    },
    TIME_WITH_TIME_ZONE {
        @Override
        public boolean supportNowValue(Database database) {
            return true;
        }

        @Override
        public String typeName() {
            return "TIME WITH TIME ZONE";
        }

        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            if (fieldMeta.javaType() != OffsetTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("TIME(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(") WITH TIME ZONE");
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != OffsetTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("CURRENT_TIME(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(")");
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != OffsetTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append(DDLUtils.zeroForTimeType(fieldMeta));
        }
    },
    DATE {
        @Override
        public boolean supportNowValue(Database database) {
            return true;
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            SQLDataTypeUtils.postgreDateNowValue(fieldMeta, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType == LocalDate.class
                    || javaType == Year.class
                    || javaType == YearMonth.class
                    || javaType == MonthDay.class) {
                builder.append(DDLUtils.zeroForTimeType(fieldMeta));
            } else {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
        }
    },
    TIMESTAMP_WITHOUT_TIME_ZONE {
        @Override
        public boolean supportNowValue(Database database) {
            return true;
        }

        @Override
        public String typeName() {
            return "TIMESTAMP WITHOUT TIME ZONE";
        }

        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            if (fieldMeta.javaType() != LocalDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("TIMESTAMP(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(") WITHOUT TIME ZONE");
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("LOCALTIMESTAMP(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(")");
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            if (fieldMeta.javaType() != LocalDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append(DDLUtils.zeroForTimeType(fieldMeta));
        }
    },
    TIMESTAMP_WITH_TIME_ZONE {
        @Override
        public boolean supportNowValue(Database database) {
            return true;
        }

        @Override
        public String typeName() {
            return "TIMESTAMP WITH TIME ZONE";
        }

        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType != ZonedDateTime.class && javaType != OffsetDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("TIMESTAMP(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(") WITH TIME ZONE");
        }

        @Override
        public void nowValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType != ZonedDateTime.class && javaType != OffsetDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append("CURRENT_TIMESTAMP(")
                    .append(SQLDataTypeUtils.obtainTimePrecision(this, fieldMeta))
                    .append(")");
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType != ZonedDateTime.class && javaType != OffsetDateTime.class) {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }
            builder.append(DDLUtils.zeroForTimeType(fieldMeta));
        }
    },
    INTERVAL {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            builder.append("INTERVAL ");

            if (javaType == Duration.class) {
                builder.append("SECOND (6)");
            } else if (javaType == Period.class) {
                builder.append("DAY");
            } else {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }

        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            Class<?> javaType = fieldMeta.javaType();
            if (javaType == Duration.class) {
                builder.append("'0 SECOND'");
            } else if (javaType == Period.class) {
                builder.append("'0 DAY'");
            } else {
                throw SQLDataTypeUtils.createNotJavaTypeException(this, fieldMeta);
            }

        }
    },
    /*################################## blow  BOOLEAN Type  ##################################*/
    BOOLEAN {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("false");
        }
    },

    /*################################## blow  Geometric Types  ##################################*/
    POINT {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'(0.0,0.0)'");
        }
    },
    LINE {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            throw SQLDataTypeUtils.createNotSupportDefaultClause(this, fieldMeta, database);
        }

        @Override
        public boolean supportZeroValue(Database database) {
            return false;
        }
    },
    LSEG {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'[(0.0,0.0),(0.0,0.0)]'");
        }
    },
    BOX {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'(0.0,0.0),(0.0,0.0)'");
        }
    },
    PATH {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'(0.0,0.0)'");
        }
    },
    POLYGON {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'(0.0,0.0)'");
        }
    },
    CIRCLE {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'<(0.0,0.0),0>'");
        }
    },
    /*##################################TODO blow  Network Address Types  ##################################*/

    /*################################## blow  Bit String Types  ##################################*/
    BIT {
        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            // postgre 11 document not specified the maximum length
            SQLDataTypeUtils.appendDataTypeWithMaxPrecision(this, fieldMeta, Integer.MAX_VALUE, builder);
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("B'0'");
        }
    },
    BIT_VARYING {
        @Override
        public String typeName() {
            return "BIT VARYING";
        }

        @Override
        public void dataTypeClause(FieldMeta<?, ?> fieldMeta, SQLBuilder builder) throws MetaException {
            int precision = fieldMeta.precision();
            builder.append(typeName());
            if (precision > 0) {
                builder.append("(")
                        .append(precision)
                        .append(")");
            }
        }

        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("B'0'");
        }
    },
    /*################################## blow JSON type ##################################*/
    JSON {
        @Override
        public void zeroValue(FieldMeta<?, ?> fieldMeta, SQLBuilder builder, Database database)
                throws MetaException {
            builder.append("'{}'");
        }
    };


    @Override
    public final Database database() {
        return Database.Postgre;
    }
}
