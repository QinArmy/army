package io.army.dialect;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class representing standard single update context.
 * </p>
 */
final class SingleUpdateContext extends _BaseSqlContext implements _SingleUpdateContext {

    static SingleUpdateContext create(_SingleUpdate update, final byte tableIndex, Dialect dialect, Visible visible) {

        return new SingleUpdateContext(update, tableIndex, dialect, visible);
    }

    final SingleTableMeta<?> table;

    final String tableAlias;

    final char[] safeTableAlias;

    final List<FieldMeta<?, ?>> fieldList;

    final List<_Expression<?>> valueExpList;
    final List<_Predicate> predicateList;

    private SingleUpdateContext(_SingleUpdate update, byte tableIndex, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        final TableMeta<?> table = update.table();
        if (update.table() instanceof ChildTableMeta) {
            String m = String.format("table[%s] isn't %s", update.table(), SingleTableMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        this.table = (SingleTableMeta<?>) table;
        this.fieldList = update.fieldList();
        this.valueExpList = update.valueExpList();
        this.predicateList = update.predicateList();

        this.tableAlias = update.tableAlias();
        this.safeTableAlias = dialect.quoteIfNeed(this.tableAlias).toCharArray();
    }

    /*################################## blow _SqlContext method ##################################*/

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (!this.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, fieldMeta);
        }
        this.appendField(fieldMeta);
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.table) {
            throw _Exceptions.unknownColumn(null, fieldMeta);
        }
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(this.safeTableAlias)
                .append(Constant.POINT)
                .append(this.dialect.quoteIfNeed(fieldMeta.columnName()));
    }

    /*################################## blow _UpdateContext method ##################################*/

    @Override
    public boolean setClauseTableAlias() {
        return this.dialect.setClauseTableAlias();
    }

    @Override
    public List<_Predicate> predicates() {
        return this.predicateList;
    }

    /*################################## blow _SingleUpdateContext method ##################################*/

    @Nullable
    @Override
    public _SetClause childSetClause() {
        // always null
        return null;
    }

    /*################################## blow _SetClause method ##################################*/

    @Override
    public SingleTableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public boolean hasSelfJoint() {
        // always false
        return false;
    }

    @Override
    public char[] safeTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public List<? extends SetTargetPart> targetParts() {
        return this.fieldList;
    }

    @Override
    public List<? extends SetValuePart> valueParts() {
        return this.valueExpList;
    }

    @Override
    public Stmt build() {
        return Stmts.simple(this.sqlBuilder.toString(), this.paramList);
    }


    @Override
    public String toString() {
        return "standard domain update";
    }


}
