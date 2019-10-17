package io.army.dialect.mysql;

import io.army.dialect.AbstractTableDDL;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

class MySQLTableDDL extends AbstractTableDDL {

    private final MySQLFunc mySQLFunc;

    MySQLTableDDL(MySQLFunc mySQLFunc) {
        Assert.notNull(mySQLFunc, "mySQLFunc required");
        this.mySQLFunc = mySQLFunc;
    }


    @Override
    protected String specifiedFuncValue(String func, FieldMeta<?, ?> fieldMeta) {
        return null;
    }

    @Override
    protected String dataTypeText(FieldMeta<?, ?> fieldMeta) {
        return null;
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

    /*#################################### ####################################################*/


}
