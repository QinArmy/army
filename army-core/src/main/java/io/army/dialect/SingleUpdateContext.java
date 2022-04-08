package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

final class SingleUpdateContext extends StmtContext implements _SingleUpdateContext {

    static SingleUpdateContext create(_SingleUpdate update, ArmyDialect dialect, Visible visible) {
        return new SingleUpdateContext(update, dialect, visible);
    }


    private final TableMeta<?> table;

    private final String tableAlias;

    private final String safeTableAlias;

    private final List<? extends SetLeftItem> leftItemList;

    private final List<? extends SetRightItem> rightItemList;

    private final List<_Predicate> predicateList;

    private final boolean supportAlias;

    private final _SingleUpdate statement;


    private SingleUpdateContext(_SingleUpdate update, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);
        this.table = update.table();
        this.tableAlias = update.tableAlias();
        if (this.table instanceof ChildTableMeta) {
            this.safeTableAlias = dialect.quoteIfNeed(_DialectUtils.parentAlias(this.tableAlias));
        } else {
            this.safeTableAlias = dialect.quoteIfNeed(this.tableAlias);
        }
        this.leftItemList = update.leftItemList();

        this.rightItemList = update.rightItemList();
        this.predicateList = update.predicateList();
        this.supportAlias = dialect.setClauseTableAlias();
        this.statement = update;
    }


    @Override
    public List<_Predicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public _SqlContext context() {
        return this;
    }

    @Override
    public List<? extends SetLeftItem> leftItemList() {
        return this.leftItemList;
    }

    @Override
    public List<? extends SetRightItem> rightItemList() {
        return this.rightItemList;
    }

    @Override
    public String safeTableAlias(final TableMeta<?> table, final String alias) {
        if (table != this.table || !this.tableAlias.equals(alias)) {
            throw _Exceptions.unknownTable(table, alias);
        }
        return this.safeTableAlias;
    }

    @Override
    public String validateField(final TableField<?> field) {
        final TableMeta<?> table = this.table, fieldTable;
        fieldTable = field.tableMeta();
        if (table instanceof ChildTableMeta) {
            if (fieldTable != ((ChildTableMeta<?>) table).parentMeta()) {
                throw _Exceptions.unknownColumn(field);
            }
        } else if (fieldTable != table) {
            throw _Exceptions.unknownColumn(field);
        } else if (field instanceof QualifiedField
                && !this.tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
            throw _Exceptions.unknownColumn(field);
        }
        return this.safeTableAlias;
    }

    @Override
    public boolean supportTableAlias() {
        return this.supportAlias;
    }

    @Override
    public boolean supportRow() {
        return this.dialect.setClauseSupportRow();
    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (!tableAlias.equals(this.tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        final TableMeta<?> table = this.table, belongOf;
        belongOf = field.tableMeta();
        if (table instanceof ChildTableMeta) {
            if (belongOf != ((ChildTableMeta<?>) table).parentMeta()) {
                throw _Exceptions.unknownColumn(field);
            }
        } else if (belongOf != table) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(Constant.SPACE);
        if (this.supportAlias) {
            sqlBuilder.append(this.safeTableAlias)
                    .append(Constant.POINT);
        }
        this.dialect.safeObjectName(field.columnName(), sqlBuilder);
    }

    @Override
    public TableMeta<?> table() {
        return this.table;
    }

    @Override
    public String safeTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public _SingleDml statement() {
        return this.statement;
    }

    @Override
    public SimpleStmt build() {
        return Stmts.dml(this.sqlBuilder.toString(), this.paramList, _DmlUtils.hasOptimistic(this.predicateList));
    }


}
