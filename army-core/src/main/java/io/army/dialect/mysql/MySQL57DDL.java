package io.army.dialect.mysql;

import io.army.criteria.MetaException;
import io.army.dialect.AbstractDDL;
import io.army.dialect.DDLContext;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.MySQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.util.Map;
import java.util.function.Function;

class MySQL57DDL extends AbstractDDL {


    private Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap;

    MySQL57DDL(MySQL57Dialect mysql) {
        super(mysql);
        Assert.notNull(mysql, "mysql required");
    }

    /*################################## blow SQL interface method ##################################*/



    /*################################## blow AbstractTableDDL template method ##################################*/

    @Override
    protected final void defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        StringBuilder tableBuilder = context.sqlBuilder();
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        }
        if (precision > MySQLDataType.DATETIME.maxPrecision()) {
            MySQL57DDLUtils.throwPrecisionException(fieldMeta);
        }
        tableBuilder.append("CURRENT_TIMESTAMP");
        if (precision > 0) {
            tableBuilder.append("(")
                    .append(precision)
                    .append(")");
        }
    }

    @Override
    protected final void dataTypeClause(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        Map<JDBCType, Function<FieldMeta<?, ?>, String>> funcMap = this.jdbcTypeFunctionMap;
        if (funcMap == null) {
            funcMap = MySQL57DDLUtils.createJdbcFunctionMap();
            this.jdbcTypeFunctionMap = funcMap;
        }

        Function<FieldMeta<?, ?>, String> function = funcMap.get(fieldMeta.mappingMeta().jdbcType());
        if (function == null) {
            throw new MetaException("Entity[%s].column[%s] not found jdbc function"
                    , fieldMeta.tableMeta().tableName()
                    , fieldMeta.fieldName()
            );
        }
        context.sqlBuilder()
                .append(" ")
                .append(function.apply(fieldMeta));
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
    protected final void nonReservedPropDefault(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        final String defaultValue = fieldMeta.defaultValue().toUpperCase();
        StringBuilder tableBuilder = context.sqlBuilder().append(" ");
        switch (defaultValue) {
            case "NOW":
            case IDomain.NOW:
                MySQL57DDLUtils.nowFunc(fieldMeta, tableBuilder);
                break;
            case IDomain.ZERO_DATE_TIME:
                tableBuilder.append(MySQL57DDLUtils.zeroDateTime(fieldMeta, zoneId()));
                break;
            case IDomain.ZERO_DATE:
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
    protected final boolean independentIndexDefinition() {
        return false;
    }

    @Override
    protected final void indexSuffix(DDLContext context) {
        context.sqlBuilder()
                .append(",\n");
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
