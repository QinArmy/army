package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.impl.inner.InnerDomainUpdate;
import io.army.criteria.impl.inner.InnerStandardDomainUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;

import java.util.Collection;

final class ParentDomainUpdateContextImpl extends AbstractSQLContext implements ParentDomainUpdateContext {


    private final InnerStandardDomainUpdate innerUpdate;

    private final ParentTableMeta<?> tableMeta;

    private final String tableAlias;

    private final Collection<FieldMeta<?, ?>> childFields;

    private boolean needQueryChild;

    ParentDomainUpdateContextImpl(DML dml, DQL dql, InnerStandardDomainUpdate update
            , Collection<FieldMeta<?, ?>> childFields) {
        super(dml, dql);
        this.innerUpdate = update;
        this.childFields = childFields;
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMata();
        this.tableMeta = childMeta.parentMeta();
        this.tableAlias = update.tableAlias();
    }


    @Override
    public final void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        if (childFields.contains(fieldMeta)) {
            this.needQueryChild = true;
            //
        } else {

        }
    }

    @Override
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if (childFields.contains(fieldMeta)) {
            this.needQueryChild = true;
            //
        } else {

        }
    }

    @Override
    public final InnerDomainUpdate innerUpdate() {
        return this.innerUpdate;
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
