package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;
import io.army.util.Exceptions;

abstract class DomainDmlContext implements _DmlContext {


    protected final TableMeta<?> tableMeta;

    protected final String tableAlias;

    protected final Dialect dialect;

    protected final _StmtContext parentContext;

    protected final SqlBuilder sqlBuilder;

    protected DomainDmlContext(TableMeta<?> tableMeta, @Nullable String tableAlias, Dialect dialect) {
        this.tableMeta = tableMeta;
        this.tableAlias = tableAlias;
        this.dialect = dialect;
        this.parentContext = null;

        this.sqlBuilder = DialectUtils.createSQLBuilder();
    }


    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (!tableAlias.equals(this.tableAlias) || fieldMeta.tableMeta() != this.tableMeta) {
            throw Exceptions.unknownColumn(tableAlias, fieldMeta);
        }
        this.sqlBuilder.append(tableAlias)
                .append('.')
                .append(fieldMeta.columnName());
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.tableMeta) {
            throw Exceptions.unknownColumn(null, fieldMeta);
        }
        final String tableAlias = this.tableAlias;
        if (tableAlias == null) {
            this.sqlBuilder.append(this.dialect.quoteIfNeed(fieldMeta.columnName()));
        } else {
            this.sqlBuilder.append(tableAlias)
                    .append('.')
                    .append(fieldMeta.columnName());
        }
    }

    @Override
    public void appendFieldPredicate(FieldPredicate predicate) {

    }

    @Override
    public void appendText(String textValue) {

    }

    @Override
    public void appendConstant(ParamMeta paramMeta, Object value) {

    }

    @Override
    public DqlDialect dql() {
        return null;
    }

    @Override
    public SqlBuilder sqlBuilder() {
        return null;
    }

    @Override
    public void appendParam(ParamValue paramValue) {

    }

    @Override
    public TableMeta<?> tableMeta() {
        return null;
    }

    @Override
    public byte tableIndex() {
        return 0;
    }

    @Override
    public String tableSuffix() {
        return null;
    }


}
