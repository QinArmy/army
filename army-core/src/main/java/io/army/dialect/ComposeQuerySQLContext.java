package io.army.dialect;

import io.army.criteria.FieldPairDualPredicate;
import io.army.criteria.TableAliasException;
import io.army.criteria.Visible;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.List;

final class ComposeQuerySQLContext implements ClauseSQLContext {

    protected final StringBuilder sqlBuilder = new StringBuilder();

    protected final Visible visible;

    ComposeQuerySQLContext(Visible visible) {
        this.visible = visible;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendText(String textValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendTextValue(MappingType mappingType, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DQL dql() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendParam(ParamWrapper paramWrapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ParamWrapper> paramList() {
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
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendFieldPair(FieldPairDualPredicate predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void currentClause(Clause clause) {
        throw new UnsupportedOperationException();
    }

}
