package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.impl.inner.InnerDomainUpdate;
import io.army.criteria.impl.inner.InnerStandardDomainUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;

import java.util.Collection;

final class StandardChildDomainUpdateContext extends AbstractSQLContext implements ChildDomainUpdateContext {

    private final InnerStandardDomainUpdate innerUpdate;

    private final ChildTableMeta<?> tableMeta;

    private final String tableAlias;

    private final Collection<FieldMeta<?, ?>> parentFields;

    StandardChildDomainUpdateContext(DML dml, DQL dql, InnerStandardDomainUpdate update
            , Collection<FieldMeta<?, ?>> parentFields) {
        super(dml, dql);
        this.innerUpdate = update;
        this.parentFields = parentFields;
        this.tableMeta = (ChildTableMeta<?>) update.tableMata();
        this.tableAlias = update.tableAlias();
    }


    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        if (parentFields.contains(fieldMeta)) {
            //
        } else {

        }
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if (parentFields.contains(fieldMeta)) {
            //
        } else {

        }
    }

    @Override
    public final InnerDomainUpdate innerUpdate() {
        return this.innerUpdate;
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
