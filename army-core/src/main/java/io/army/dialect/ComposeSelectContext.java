package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.ArrayList;
import java.util.List;

final class ComposeSelectContext implements SelectContext {

    static ComposeSelectContext build(Dialect dialect, Visible visible) {
        return new ComposeSelectContext(dialect, visible);
    }

    private final Dialect dialect;

    private final Visible visible;

    private final SQLBuilder sqlBuilder;

    private final List<ParamWrapper> paramList;

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
    public TableContext tableContext() {
        return TableContext.EMPTY;
    }

    @Override
    public List<ParamWrapper> paramList() {
        return this.paramList;
    }

    @Override
    public DQL dql() {
        return this.dialect;
    }

    @Override
    public SQLBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList);
    }

    @Override
    public TableContextSQLContext parentContext() {
        return null;
    }

    @Override
    public void appendText(String textValue) {
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(textValue));
    }

    @Override
    public final TableContext primaryTableContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TableContext parentTableContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String primaryRouteSuffix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendParam(ParamWrapper paramWrapper) {
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
