package io.army.dialect.mysql;

import io.army.dialect.Dialect;
import io.army.dialect.ddl.AbstractTableDDL;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class MySQLTableDDL extends AbstractTableDDL {

    private final Dialect dialect;

    MySQLTableDDL(Dialect dialect) {
        Assert.notNull(dialect, "dialect required");
        Assert.isAssignable(MySQLDialect.class, dialect.getClass());
        this.dialect = dialect;
    }

    @Nullable
    @Override
    protected String keyDefinition(TableMeta<?> tableMeta) {
        for (IndexMeta<?> indexMeta : tableMeta.indexCollection()) {

        }
        return null;
    }


    @Nonnull
    @Override
    protected String tableOptions(TableMeta<?> tableMeta) {
        return null;
    }


    @Nonnull
    @Override
    protected Dialect dialect() {
        return dialect;
    }

    @Override
    protected void appendTableOptions(StringBuilder builder, TableMeta<?> tableMeta) {

    }


}
