package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.*;
import io.army.stmt.ParamValue;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;

import java.util.List;

final class SingleDeleteContext extends _BaseSqlContext implements _SingleDeleteContext {

    private final boolean unionUpdateChild;

    final SingleTableMeta<?> table;

    final String tableAlias;

    final String safeTableAlias;

    private final List<_Predicate> predicateList;

    private SingleDeleteContext(_SingleDelete delete, Dialect dialect, byte tableIndex, Visible visible) {
        super(dialect, tableIndex, visible);
        final TableMeta<?> table = delete.table();
        final String tableAlias = delete.tableAlias();
        this.unionUpdateChild = dialect.multiTableUpdateChild();

        if (table instanceof ChildTableMeta) {
            this.table = ((ChildTableMeta<?>) table).parentMeta();
            this.tableAlias = _DialectUtils.parentAlias(tableAlias);
            this.safeTableAlias = this.tableAlias;
        } else {
            this.table = (SingleTableMeta<?>) table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = dialect.quoteIfNeed(tableAlias);
        }
        this.predicateList = delete.predicateList();

        if (this.unionUpdateChild) {

        } else {

        }

    }


    @Override
    public SingleTableMeta<?> table() {
        return null;
    }

    @Override
    public String tableAlias() {
        return null;
    }

    @Override
    public String safeTableAlias() {
        return null;
    }

    @Override
    public List<_Predicate> predicates() {
        return null;
    }


    @Override
    public _Block childBlock() {
        return null;
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

    private static final class UnionDeleteChild implements _Block {

        private final ChildTableMeta<?> table;

        private final String tableAlias;

        private final String safeTableAlias;

        private final SingleDeleteContext parentContext;

        private UnionDeleteChild(ChildTableMeta<?> table, String tableAlias, SingleDeleteContext parentContext) {
            this.table = table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = parentContext.dialect.quoteIfNeed(tableAlias);
            this.parentContext = parentContext;
        }

        @Override
        public ChildTableMeta<?> table() {
            return null;
        }

        @Override
        public String tableAlias() {
            return null;
        }

        @Override
        public String safeTableAlias() {
            return null;
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> field) {

        }

        @Override
        public void appendField(FieldMeta<?, ?> field) {

        }

        @Override
        public void appendIdentifier(String identifier) {

        }

        @Override
        public void appendConstant(ParamMeta paramMeta, Object value) {

        }

        @Override
        public Dialect dialect() {
            return null;
        }

        @Override
        public StringBuilder sqlBuilder() {
            return null;
        }

        @Override
        public void appendParam(ParamValue paramValue) {

        }

        @Override
        public Visible visible() {
            return null;
        }


    }


}
