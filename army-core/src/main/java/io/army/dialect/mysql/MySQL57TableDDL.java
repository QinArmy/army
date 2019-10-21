package io.army.dialect.mysql;

import io.army.dialect.AbstractTableDDL;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class MySQL57TableDDL extends AbstractTableDDL {

    private final MySQLFunc mySQLFunc;


    private final Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap;

    MySQL57TableDDL(MySQLFunc mySQLFunc) {
        Assert.notNull(mySQLFunc, "mySQLFunc required");
        this.mySQLFunc = mySQLFunc;
        this.jdbcTypeFunctionMap = Collections.unmodifiableMap(jdbcTypeFunctionMap());
    }


    @Override
    protected String nowFunc(String func, FieldMeta<?, ?> fieldMeta) {
        return mySQLFunc.now(MySQLDDLUtils.getNumberPrecision(fieldMeta, 0, 6));
    }

    @Override
    protected String dataTypeText(FieldMeta<?, ?> fieldMeta) {
        Function<FieldMeta<?, ?>, String> function = jdbcTypeFunctionMap.get(fieldMeta.mappingType().jdbcType());
        Assert.notNull(function, () -> String.format("Entity[%s].column[%s] not found jdbc function",
                fieldMeta.table().tableName(),
                fieldMeta.fieldName()
        ));
        return function.apply(fieldMeta);
    }

    @Override
    protected void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta) {
        builder.append("ENGINE = InnoDB CHARACTER SET = ")
                .append(tableMeta.charset())
                .append(" COMMON '")
                .append(tableMeta.comment())
                .append("'")
        ;
    }

    /*################################## blow protected method ##################################*/

    protected Map<JDBCType, Function<FieldMeta<?, ?>, String>> jdbcTypeFunctionMap() {
        Map<JDBCType, Function<FieldMeta<?, ?>, String>> map = new HashMap<>();

        // below  numeric type

        map.put(JDBCType.BIT, MySQLDDLUtils::bitFunction);
        map.put(JDBCType.TINYINT, MySQLDDLUtils::tinyIntFunction);
        map.put(JDBCType.BOOLEAN, MySQLDDLUtils::booleanFunction);
        map.put(JDBCType.SMALLINT, MySQLDDLUtils::smallIntFunction);

        map.put(JDBCType.INTEGER, MySQLDDLUtils::intFunction);
        map.put(JDBCType.BIGINT, MySQLDDLUtils::bigIntFunction);
        map.put(JDBCType.DECIMAL, MySQLDDLUtils::decimalFunction);
        map.put(JDBCType.FLOAT, MySQLDDLUtils::floatFunction);

        map.put(JDBCType.DOUBLE, MySQLDDLUtils::doubleFunction);

        // below data time type
        map.put(JDBCType.DATE, MySQLDDLUtils::dateFunction);
        map.put(JDBCType.TIME, MySQLDDLUtils::timeFunction);
        map.put(JDBCType.TIMESTAMP, MySQLDDLUtils::timestampFunction);

        // below string type

        map.put(JDBCType.CHAR, MySQLDDLUtils::charFunction);
        map.put(JDBCType.VARCHAR, MySQLDDLUtils::varcharFunction);
        map.put(JDBCType.BINARY, MySQLDDLUtils::binaryFunction);
        map.put(JDBCType.VARBINARY, MySQLDDLUtils::varbinaryFunction);

        map.put(JDBCType.BLOB, MySQLDDLUtils::blobFunction);

        return map;
    }





    /*################################## blow private method ##################################*/


}
