package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;

import java.util.List;

final class StandardDeleteContext extends _BaseSqlContext implements _SingleDeleteContext {

    static StandardDeleteContext create(_SingleDelete delete, Dialect dialect, Visible visible) {
        return new StandardDeleteContext(delete, dialect, visible);
    }


    final boolean unionUpdateChild;

    final SingleTableMeta<?> table;

    final String tableAlias;

    final String safeTableAlias;

    final List<_Predicate> predicateList;

    final _Block childBlock;

    private StandardDeleteContext(_SingleDelete delete, Dialect dialect, Visible visible) {
        super(dialect, visible);
        final TableMeta<?> table = delete.table();
        final String tableAlias = delete.tableAlias();
        this.unionUpdateChild = dialect.multiTableUpdateChild();
        if (table instanceof ChildTableMeta) {
            this.table = ((ChildTableMeta<?>) table).parentMeta();
            this.tableAlias = _DialectUtils.parentAlias(tableAlias);
            this.safeTableAlias = this.tableAlias;
            this.childBlock = new ChildBlock((ChildTableMeta<?>) table, tableAlias, this);
        } else {
            this.table = (SingleTableMeta<?>) table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = dialect.quoteIfNeed(tableAlias);
            this.childBlock = null;
        }
        this.predicateList = delete.predicateList();

    }


    @Override
    public SingleTableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public String safeTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public List<_Predicate> predicates() {
        return this.predicateList;
    }


    @Override
    public _Block childBlock() {
        return this.childBlock;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {

    }

    @Override
    public void appendField(FieldMeta<?, ?> field) {

    }

    @Override
    public Stmt build() {
        return Stmts.simple(this.sqlBuilder.toString(), this.paramList);
    }

    private static final class ChildBlock implements _Block {

        private final ChildTableMeta<?> table;

        private final String tableAlias;

        private final String safeTableAlias;

        private final StandardDeleteContext parentContext;

        private ChildBlock(ChildTableMeta<?> table, String tableAlias, StandardDeleteContext parentContext) {
            this.table = table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = parentContext.dialect.quoteIfNeed(tableAlias);
            this.parentContext = parentContext;
        }

        @Override
        public ChildTableMeta<?> table() {
            return this.table;
        }

        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public String safeTableAlias() {
            return this.safeTableAlias;
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> field) {

        }

        @Override
        public void appendField(FieldMeta<?, ?> field) {

        }

        @Override
        public Dialect dialect() {
            return this.parentContext.dialect;
        }

        @Override
        public StringBuilder sqlBuilder() {
            return this.parentContext.sqlBuilder;
        }

        @Override
        public void appendParam(ParamValue paramValue) {
            this.parentContext.appendParam(paramValue);
        }

        @Override
        public Visible visible() {
            return this.parentContext.visible;
        }


    }


}
