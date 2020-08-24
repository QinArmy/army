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

    protected SQLBuilder sqlBuilder = DialectUtils.createSQLBuilder();

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
    public final SQLBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final void appendTable() {
        this.sqlBuilder.append(" ");
        String tableName = this.tableMeta.tableName();
        if (this.tableSuffix != null) {
            tableName += this.tableSuffix;
        }
        this.sqlBuilder.append(this.dialect.quoteIfNeed(tableName));

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
    public final void appendFieldWithTable(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.tableMeta) {
            throw DialectUtils.createUnKnownFieldException(fieldMeta);
        }
        appendTable();
        this.sqlBuilder.append(".")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    public final void appendIdentifier(String identifier) {
        this.sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(identifier));
    }

    @Override
    public final void resetBuilder() {
        this.sqlList.add(this.sqlBuilder.toString());
        this.sqlBuilder = DialectUtils.createSQLBuilder();
    }

    @Override
    public final List<String> build() {
        Assert.state(!this.prepared, "");
        this.prepared = true;
        return Collections.unmodifiableList(this.sqlList);
    }
}