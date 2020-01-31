package io.army.dialect.mysql;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.AbstractTableDDL;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.MySQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.util.Map;
import java.util.function.Function;

class MySQL57TableDDL extends AbstractTableDDL {



    private final Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap;

    MySQL57TableDDL(MySQL57Dialect mysql) {
        super(mysql);
        Assert.notNull(mysql, "mysql required");
        this.jdbcTypeFunctionMap = MySQL57DDLUtils.createJdbcFunctionMap();
    }

    /*################################## blow SQL interface method ##################################*/



    /*################################## blow AbstractTableDDL template method ##################################*/

    @Override
    protected final String defaultOfCreateAndUpdate(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        }
        if (precision > MySQLDataType.DATETIME.maxPrecision()) {
            MySQL57DDLUtils.throwPrecisionException(fieldMeta);
        }
        String exp = "CURRENT_TIMESTAMP";
        if (precision > 0) {
            exp = exp + "(" + precision + ")";
        }
        return exp;
    }

    @Override
    protected String dataTypeClause(FieldMeta<?, ?> fieldMeta) {
        Function<FieldMeta<?, ?>, String> function = jdbcTypeFunctionMap.get(fieldMeta.mappingType().jdbcType());
        if (function == null) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].column[%s] not found jdbc function"
                    , fieldMeta.table().tableName()
                    , fieldMeta.fieldName()
            );
        }
        return function.apply(fieldMeta);
    }

    @Override
    protected void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta) {
        builder.append("ENGINE = InnoDB CHARACTER SET = ")
                .append(MySQL57DDLUtils.tableCharset(tableMeta.charset()))
                .append(" COMMENT '")
                .append(tableMeta.comment())
                .append("'")
        ;
    }

    @Override
    protected final String nonRequiredPropDefault(FieldMeta<?, ?> fieldMeta) {
        String defaultValue = fieldMeta.defaultValue().toUpperCase();
        switch (defaultValue) {
            case "NOW":
            case "NOW()":
                defaultValue = defaultValue.replace("NOW", "CURRENT_TIMESTAMP");
                break;
            case IDomain.ZERO_DATE_TIME:
                defaultValue = MySQL57DDLUtils.zeroDateTime(fieldMeta, zoneId());
                break;
            case IDomain.ZERO_DATE:
                defaultValue = MySQL57DDLUtils.zeroDate(fieldMeta, zoneId());
                break;
            case IDomain.ZERO_YEAR:
                defaultValue = MySQL57DDLUtils.zeroYear(fieldMeta, zoneId());
                break;
            default:
                defaultValue = handleDefaultValue(fieldMeta);
        }
        return defaultValue;
    }

    @Override
    protected boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta) {
        return !fieldMeta.primary()
                &&  !MySQL57DDLUtils.NO_DEFAULT_JDBC.contains(fieldMeta.jdbcType());
    }

    /*################################## blow protected method ##################################*/

    /**
     * handle default value . invoked by {@link #nonRequiredPropDefault(FieldMeta)}
     */
    String handleDefaultValue(FieldMeta<?, ?> fieldMeta) {
        return MySQL57DDLUtils.quoteDefaultIfNeed(fieldMeta);
    }


    /*################################## blow private method ##################################*/


}
