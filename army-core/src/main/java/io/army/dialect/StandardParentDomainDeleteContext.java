package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerDomainDelete;
import io.army.criteria.impl.inner.InnerStandardDomainDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;


final class StandardParentDomainDeleteContext extends AbstractSQLContext implements ParentDomainDeleteContext {

    private final InnerStandardDomainDelete innerDelete;

    private final ParentTableMeta<?> tableMeta;

    StandardParentDomainDeleteContext(Dialect dialect, Visible visible
            , InnerStandardDomainDelete delete) {
        super(dialect, visible);
        this.innerDelete = delete;
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();
        this.tableMeta = childMeta.parentMeta();
    }

    @Override
    public final ParentTableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final InnerDomainDelete innerDelete() {
        return this.innerDelete;
    }

    @Override
    public void appendTable(TableMeta<?> tableMeta) {

    }


    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {

    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {

    }

    @Override
    public final String tableAlias() {
        return this.innerDelete.tableAlias();
    }
}
