package io.army.dialect;

import io.army.criteria.DataField;
import io.army.criteria.StandardStatement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;

import java.util.List;

final class DomainUpdateContext extends SingleDmlContext implements _SingleUpdateContext {


    static _SingleUpdateContext nonChild(_SingleUpdate stmt, ArmyDialect dialect, Visible visible) {
        if (!(stmt instanceof StandardStatement)) {
            throw new IllegalArgumentException();
        }
        final TableMeta<?> domainTable;
        domainTable = stmt.table();
        final String tableAlias = stmt.tableAlias();
        final _SingleUpdateContext context;
        if (domainTable instanceof ChildTableMeta) {
            final ParentTableMeta<?> parent = ((ChildTableMeta<?>) domainTable).parentMeta();
            context = new DomainUpdateContext(parent, _DialectUtils.parentAlias(tableAlias), stmt, dialect, visible);
        } else {
            context = new DomainUpdateContext(domainTable, tableAlias, stmt, dialect, visible);
        }
        return context;
    }

    static _SingleUpdateContext child(_SingleUpdate stmt, ArmyDialect dialect, Visible visible) {
        if (!(stmt instanceof StandardStatement)) {
            throw new IllegalArgumentException();
        }
        final TableMeta<?> domainTable;
        domainTable = stmt.table();
        if (!(domainTable instanceof ChildTableMeta)) {
            throw new IllegalArgumentException();
        }
        return new DomainUpdateContext(domainTable, stmt.tableAlias(), stmt, dialect, visible);
    }


    private final TableMeta<?> relationalTable;

    private final String relationalAlias;

    private final String safeRelationalAlais;

    private DomainUpdateContext(TableMeta<?> table, String alias, _SingleUpdate stmt
            , ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);

        final TableMeta<?> domainTable;
        domainTable = stmt.table();
        final String tableAlias = stmt.tableAlias();

        if (!(domainTable instanceof ChildTableMeta)) {
            this.relationalTable = null;
            this.relationalAlias = null;
            this.safeRelationalAlais = null;
        } else if (this.table == domainTable) {
            this.relationalTable = ((ChildTableMeta<?>) domainTable).parentMeta();
            this.relationalAlias = _DialectUtils.parentAlias(tableAlias);
            this.safeRelationalAlais = this.dialect.identifier(this.relationalAlias);
        } else {
            this.relationalTable = domainTable;
            this.relationalAlias = tableAlias;
            this.safeRelationalAlais = this.dialect.identifier(tableAlias);
        }


    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {

    }

    @Override
    public void appendField(final FieldMeta<?> field) {

    }

    @Override
    public void appendSetLeftItem(final DataField field) {

    }

    @Override
    public SimpleStmt build() {
        return null;
    }

    @Override
    public BatchStmt build(final List<?> paramList) {
        return null;
    }


}
