package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardDomainUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;

import java.util.Collection;

final class StandardChildDomainUpdateContext extends AbstractTableContextSQLContext implements ChildDomainUpdateContext {

    private final InnerStandardDomainUpdate innerUpdate;

    private final ChildTableMeta<?> tableMeta;

    private final String tableAlias;

    private final Collection<FieldMeta<?, ?>> parentFields;

    StandardChildDomainUpdateContext(Dialect dialect, Visible visible, InnerStandardDomainUpdate update
            , Collection<FieldMeta<?, ?>> parentFields) {
        super(dialect, visible, null);

        this.innerUpdate = update;
        this.parentFields = parentFields;
        this.tableMeta = (ChildTableMeta<?>) update.tableMeta();
        this.tableAlias = update.tableAlias();
    }



    @Override
    public final ChildTableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }
}
