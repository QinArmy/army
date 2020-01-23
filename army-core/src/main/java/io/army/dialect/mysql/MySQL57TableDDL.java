package io.army.dialect.mysql;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.AbstractTableDDL;
import io.army.dialect.SQL;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.MySQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class MySQL57TableDDL extends AbstractTableDDL {

    private final SQL mysql;

    private final Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap;

    MySQL57TableDDL(MySQL57Dialect mysql) {
        Assert.notNull(mysql, "mysql required");
        this.jdbcTypeFunctionMap = MySQL57DDLUtils.createJdbcFunctionMap();
        this.mysql = mysql;
    }

    /*################################## blow SQL interface method ##################################*/

    @Override
    public String quoteIfNeed(String text) {
        return mysql.quoteIfNeed(text);
    }

    @Override
    public boolean isKeyWord(String text) {
        return mysql.isKeyWord(text);
    }

    @Override
    public Map<String, List<String>> standardFunc() {
        return mysql.standardFunc();
    }

    @Override
    public ZoneId zoneId() {
        return mysql.zoneId();
    }

    /*################################## blow AbstractTableDDL template method ##################################*/

    @Override
    protected String createUpdateDefault(FieldMeta<?, ?> fieldMeta) {
        int precision = fieldMeta.precision();
        if (precision < 0) {
            precision = 0;
        } else if (precision > MySQLDataType.DATETIME.maxPrecision()) {
            MySQL57DDLUtils.throwPrecisionException(fieldMeta);
        }
        return "CURRENT_TIMESTAMP(" + precision + ")";
    }

    @Override
    protected String dataTypeText(FieldMeta<?, ?> fieldMeta) {
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
                .append(tableMeta.charset())
                .append(" COMMENT '")
                .append(tableMeta.comment())
                .append("'")
        ;
    }

    @Override
    protected String nonRequiredPropDefault(FieldMeta<?, ?> fieldMeta) {
        String defaultValue = fieldMeta.defaultValue().toUpperCase();
        switch (defaultValue) {
            case "LOCAL_NOW":
            case "LOCAL_NOW()":
                defaultValue = defaultValue.replace("LOCAL_NOW", "CURRENT_TIMESTAMP");
                break;
            case IDomain.SOURCE_DATE_TIME:
                defaultValue = MySQL57DDLUtils.zeroDateTime(fieldMeta, zoneId());
                break;
            case IDomain.SOURCE_DATE:
                defaultValue = MySQL57DDLUtils.zeroDate(fieldMeta, zoneId());
                break;
            default:
        }
        return defaultValue;
    }


    /*################################## blow protected method ##################################*/




    /*################################## blow private method ##################################*/


}
