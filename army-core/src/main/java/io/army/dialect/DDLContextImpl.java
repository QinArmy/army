package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DDLContextImpl implements DDLContext {

    protected final Dialect dialect;

    protected StringBuilder sqlBuilder = new StringBuilder();

    protected final TableMeta<?> tableMeta;

    private final String tableSuffix;

    protected final List<String> sqlList = new ArrayList<>();

    private boolean prepared;

    DDLContextImpl(Dialect dialect, TableMeta<?> tableMeta, @Nullable String tableSuffix) {
        this.dialect = dialect;
        this.tableMeta = tableMeta;
        this.tableSuffix = tableSuffix;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final void appendTable() {
        this.sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(this.tableMeta.tableName()));
        if (this.tableSuffix != null) {
            this.sqlBuilder.append(this.tableSuffix);
        }
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.tableMeta) {
            throw DialectUtils.createUnKnownFieldException(fieldMeta);
        }
        this.sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));

    }

    @Override
    public final void append(String sql) {
        this.sqlList.add(sql);
    }

    @Override
    public final void resetBuilder() {
        this.sqlBuilder = new StringBuilder();
    }

    @Override
    public final List<String> build() {
        Assert.state(!this.prepared, "");
        this.prepared = true;
        return Collections.unmodifiableList(this.sqlList);
    }
}
