package io.army.dialect.mysql;

import io.army.criteria.MetaException;
import io.army.dialect.AbstractDDL;
import io.army.dialect.DDLContext;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

class MySQL57DDL extends AbstractDDL {


    private Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap;

    MySQL57DDL(MySQL57Dialect mysql) {
        super(mysql);
        Assert.notNull(mysql, "mysql required");
    }

    /*################################## blow SQL interface method ##################################*/

    /*################################## blow DDL method ##################################*/

    @Override
    public void clearForDDL() {
        super.clearForDDL();
        this.jdbcTypeFunctionMap = null;
    }

    /*################################## blow AbstractTableDDL template method ##################################*/

    @Override
    protected final void defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder builder = context.sqlBuilder();
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        }
        if (precision > 6) {
            throw new MetaException("%s,this value range of MySQL DATETIME is [0,6] .", fieldMeta);
        }
        builder.append("CURRENT_TIMESTAMP");
        if (precision > 0) {
            builder.append("(")
                    .append(precision)
                    .append(")");
        }
    }

    @Override
    protected Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap() {
        return MySQL57DDLUtils.createJdbcFunctionMap();
    }

    @Override
    protected void tableOptionsClause(DDLContext context) {
        TableMeta<?> tableMeta = context.tableMeta();
        context.sqlBuilder()
                .append(" ENGINE = InnoDB CHARACTER SET = ")
                .append(MySQL57DDLUtils.tableCharset(tableMeta.charset()))
                .append(" COMMENT '")
                .append(tableMeta.comment())
                .append("'")
        ;
    }

    @Override
    protected final void doFieldDefaultValue(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        final String defaultValue = fieldMeta.defaultValue().toUpperCase();
        StringBuilder tableBuilder = context.sqlBuilder().append(" ");
        switch (defaultValue) {
            case IDomain.NOW:
                if (fieldMeta.javaType() != LocalDateTime.class) {
                    throw new MetaException("%s, IDomain.NOW only support LocalDateTime for MySQL");
                }
                MySQL57DDLUtils.nowFunc(fieldMeta, tableBuilder);
                break;
            case IDomain.ZERO_DATE_TIME:
                if (fieldMeta.javaType() != LocalDateTime.class) {
                    throw new MetaException("%s, IDomain.ZERO_DATE_TIME only support LocalDateTime for MySQL");
                }
                tableBuilder.append(MySQL57DDLUtils.zeroDateTime(fieldMeta, zoneId()));
                break;
            case IDomain.ZERO_DATE:
                if (fieldMeta.javaType() != LocalDate.class) {
                    throw new MetaException("%s, IDomain.ZERO_DATE only support LocalDate for MySQL");
                }
                tableBuilder.append(MySQL57DDLUtils.zeroDate(fieldMeta, zoneId()));
                break;
            case IDomain.ZERO_YEAR:
                tableBuilder.append(MySQL57DDLUtils.zeroYear(fieldMeta, zoneId()));
                break;
            default:
                tableBuilder.append(handleDefaultValue(fieldMeta));
        }
    }

    @Override
    protected final boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta) {
        return !fieldMeta.primary()
                && !MySQL57DDLUtils.NO_DEFAULT_JDBC.contains(fieldMeta.mappingMeta().jdbcType());
    }

    @Override
    protected final boolean useIndependentIndexDefinition() {
        return false;
    }

    @Override
    protected final boolean useIndependentComment() {
        return false;
    }

    /*################################## blow protected method ##################################*/

    /**
     * handle default value .
     */
    String handleDefaultValue(FieldMeta<?, ?> fieldMeta) {
        return MySQL57DDLUtils.quoteDefaultIfNeed(fieldMeta, escapeQuote(fieldMeta.defaultValue()));
    }


    /*################################## blow private method ##################################*/


}
