package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;

import java.util.ArrayList;
import java.util.List;

final class ComposeSelectContext implements SelectContext {

    static ComposeSelectContext build(Dialect dialect, Visible visible) {
        return new ComposeSelectContext(dialect, visible);
    }

    private final Dialect dialect;

    private final Visible visible;

    private final SqlBuilder sqlBuilder;

    private final List<ParamValue> paramList;

    ComposeSelectContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = DialectUtils.createSQLBuilder();
        this.paramList = new ArrayList<>();
    }

    @Override
    public void appendFieldPredicate(FieldPredicate predicate) {
        predicate.appendPredicate(this);
    }

    @Override
    public Visible visible() {
        return this.visible;
    }

    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    @Override
    public TablesContext tableContext() {
        return TablesContext.EMPTY;
    }

    @Override
    public List<ParamValue> paramList() {
        return this.paramList;
    }

    public DqlDialect dialect() {
        return this.dialect;
    }

    @Override
    public SqlBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public SimpleStmt build() {
        return null;
    }

    @Override
    public _TablesSqlContext parentContext() {
        return null;
    }

    @Override
    public void appendIdentifier(String identifier) {
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(identifier));
    }

    @Override
    public final TablesContext primaryTableContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TablesContext parentTableContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String primaryRouteSuffix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendParam(ParamValue paramValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendTable(TableMeta<?> tableMeta,@Nullable String tableAlias) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendParentOf(ChildTableMeta<?> childTableMeta,String childAlis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendConstant(ParamMeta paramMeta, Object value) {
        throw new UnsupportedOperationException();
    }

}
