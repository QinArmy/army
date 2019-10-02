package io.army.dialect.mysql;

import io.army.dialect.Dialect;
import io.army.dialect.ddl.AbstractTableDDL;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import javax.annotation.Nonnull;

abstract class MySQLTableDDL extends AbstractTableDDL {

    private final Dialect dialect;

    MySQLTableDDL(Dialect dialect) {
        Assert.notNull(dialect, "dialect required");
        Assert.isAssignable(MySQLDialect.class, dialect.getClass());
        this.dialect = dialect;
    }


    @Nonnull
    @Override
    protected Dialect dialect() {
        return dialect;
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
