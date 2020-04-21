package io.army.dialect;

import io.army.criteria.impl.inner.InnerStandardSingleUpdate;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.meta.TableMeta;

final class StandardUpdateContext extends AbstractSQLContext implements UpdateContext {

    private final InnerStandardSingleUpdate innerUpdate;

    StandardUpdateContext(DML dml, DQL dql, InnerStandardSingleUpdate update) {
        super(dml, dql);
        this.innerUpdate = update;
    }

    @Override
    public final InnerUpdate innerUpdate() {
        return this.innerUpdate;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.innerUpdate.tableMata();
    }

    @Override
    public final String tableAlias() {
        return this.innerUpdate.tableAlias();
    }

}
