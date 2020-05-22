package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.DomainSQLWrapper;
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

    private final StringBuilder sqlBuilder;

    private final List<ParamWrapper> paramList;

    ComposeSelectContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder();
        this.paramList = new ArrayList<>();
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
    public StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList);
    }

    @Override
    public DomainSQLWrapper build(DomainWrapper domainWrapper) {
        return DomainSQLWrapper.build(this.sqlBuilder.toString(), this.paramList, domainWrapper);
    }

    @Override
    public void appendText(String textValue) {
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(textValue));
    }

    @Override
    public void appendParam(ParamWrapper paramWrapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendTable(TableMeta<?> tableMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendParentTableOf(ChildTableMeta<?> childTableMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendSpecialPredicate(FieldPredicate predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendFieldPair(LeftFieldPairDualPredicate predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendTextValue(MappingMeta mappingType, Object value) {
        throw new UnsupportedOperationException();
    }
}
