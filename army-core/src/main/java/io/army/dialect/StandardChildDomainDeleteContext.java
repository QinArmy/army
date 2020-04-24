package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardDomainDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;


final class StandardChildDomainDeleteContext extends AbstractClauseContext implements ChildDomainDeleteContext {

    private final InnerStandardDomainDelete innerDelete;

    private final ChildTableMeta<?> tableMeta;


    StandardChildDomainDeleteContext(Dialect dialect, Visible visible
            , InnerStandardDomainDelete innerDelete) {
        super(dialect, visible);
        this.innerDelete = innerDelete;
        this.tableMeta = (ChildTableMeta<?>) innerDelete.tableMeta();
    }

    @Override
    public final ChildTableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public void currentClause(Clause clause) {

    }

    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {

    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {

    }

    @Override
    public String tableAlias() {
        return innerDelete.tableAlias();
    }
}
