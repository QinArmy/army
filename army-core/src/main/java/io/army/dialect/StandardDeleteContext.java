package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

final class StandardDeleteContext extends SingleDmlContext implements _SingleDeleteContext {

    static StandardDeleteContext create(_SingleDelete delete, ArmyDialect dialect, Visible visible) {
        return new StandardDeleteContext(delete, dialect, visible);
    }

    final ChildBlock childBlock;

    private StandardDeleteContext(_SingleDelete delete, ArmyDialect dialect, Visible visible) {
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
    public String safeTableAlias(final TableMeta<?> table, final String alias) {
        final ChildBlock childBlock = this.childBlock;
        final String safeTableAlias;
        if (childBlock == null) {
            if (table != this.table || !this.tableAlias.equals(alias)) {
                throw _Exceptions.unknownTable(table, alias);
            }
            safeTableAlias = this.safeTableAlias;
        } else if (table == childBlock.table && childBlock.tableAlias.equals(alias)) {
            safeTableAlias = childBlock.safeTableAlias;
        } else {
            throw _Exceptions.unknownTable(table, alias);
        }
        return safeTableAlias;
    }


    @Override
    public boolean multiTableUpdateChild() {
        return this.supportMultiTableDml;
    }

    @Override
    public ChildBlock childBlock() {
        return this.childBlock;
    }



}
