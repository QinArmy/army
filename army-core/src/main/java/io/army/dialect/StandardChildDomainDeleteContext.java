package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardDomainDelete;
import io.army.meta.ChildTableMeta;


final class StandardChildDomainDeleteContext extends AbstractTableContextSQLContext implements ChildDomainDeleteContext {

    private final InnerStandardDomainDelete innerDelete;

    private final ChildTableMeta<?> tableMeta;


    StandardChildDomainDeleteContext(Dialect dialect, Visible visible
            , InnerStandardDomainDelete innerDelete) {
        super(dialect, visible, null);
        this.innerDelete = innerDelete;
        this.tableMeta = (ChildTableMeta<?>) innerDelete.tableMeta();
    }

    @Override
    public final ChildTableMeta<?> tableMeta() {
        return this.tableMeta;
    }


    @Override
    public String tableAlias() {
        return innerDelete.tableAlias();
    }
}
