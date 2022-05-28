package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.TableMeta;

abstract class SingleTableContext extends StmtContext implements _SingleTableContext {

    final TableMeta<?> table;

    final String alias;

    final String safeAlias;

    SingleTableContext(TableMeta<?> table, String alias, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);
        this.table = table;
        this.alias = alias;
        this.safeAlias = dialect.identifier(alias);
    }

    SingleTableContext(TableMeta<?> table, String alias, StmtContext outerContext) {
        super(outerContext);
        this.table = table;
        this.alias = alias;
        this.safeAlias = this.dialect.identifier(alias);
    }

    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final String safeAlias() {
        return this.safeAlias;
    }


}
