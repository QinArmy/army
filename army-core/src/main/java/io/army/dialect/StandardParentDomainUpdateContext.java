package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardDomainUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;

import java.util.Collection;

final class StandardParentDomainUpdateContext extends AbstractTableContextSQLContext implements ParentDomainUpdateContext {


    private final InnerStandardDomainUpdate innerUpdate;

    private final ParentTableMeta<?> tableMeta;

    private final String tableAlias;

    private final Collection<FieldMeta<?, ?>> childFields;

    private boolean needQueryChild;

    StandardParentDomainUpdateContext(Dialect dialect, Visible visible, InnerStandardDomainUpdate update
            , Collection<FieldMeta<?, ?>> childFields) {
        super(dialect, visible, null);
        this.innerUpdate = update;
        this.childFields = childFields;
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        this.tableMeta = childMeta.parentMeta();
        this.tableAlias = update.tableAlias();
    }


    @Override
    public final ParentTableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    @Override
    public final boolean needQueryChild() {
        return this.needQueryChild;
    }
}
