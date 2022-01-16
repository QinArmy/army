package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;

final class StandardDeleteContext extends _SingleDmlContext implements _SingleDeleteContext {

    static StandardDeleteContext create(_SingleDelete delete, _Dialect dialect, Visible visible) {
        return new StandardDeleteContext(delete, dialect, visible);
    }

    final ChildBlock childBlock;

    private StandardDeleteContext(_SingleDelete delete, _Dialect dialect, Visible visible) {
        super(delete, dialect, visible);
        final TableMeta<?> table = delete.table();
        final String tableAlias = delete.tableAlias();
        if (table instanceof ChildTableMeta) {
            this.childBlock = new ChildBlock((ChildTableMeta<?>) table, tableAlias, this);
        } else {
            this.childBlock = null;
        }
    }

    @Override
    public boolean multiTableUpdateChild() {
        return this.multiTableUpdateChild;
    }

    @Override
    public ChildBlock childBlock() {
        return this.childBlock;
    }



}
