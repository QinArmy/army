package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;


final class StandardParentDomainDeleteContext extends AbstractTableContextSQLContext implements ParentDomainDeleteContext {

    private final InnerStandardDomainDelete innerDelete;

    private final ParentTableMeta<?> tableMeta;

    StandardParentDomainDeleteContext(Dialect dialect, Visible visible
            , InnerStandardDomainDelete delete) {
        super(dialect, visible, null);
        this.innerDelete = delete;
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();
        this.tableMeta = childMeta.parentMeta();
    }

    @Override
    public final ParentTableMeta<?> tableMeta() {
        return this.tableMeta;
    }


    @Override
    public final String tableAlias() {
        return this.innerDelete.tableAlias();
    }
}
