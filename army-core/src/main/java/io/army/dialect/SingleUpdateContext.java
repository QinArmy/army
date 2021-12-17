package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class representing standard single update context.
 * </p>
 */
final class SingleUpdateContext extends DomainDmlContext implements _DmlContext {

    static SingleUpdateContext create(_SingleUpdate update, final byte tableIndex, Dialect dialect, Visible visible) {
        if (update.table() instanceof ChildTableMeta) {
            String m = String.format("table[%s] isn't %s", update.table(), SingleTableMeta.class.getName());
            throw new IllegalArgumentException(m);
        }
        return new SingleUpdateContext(update, tableIndex, dialect, visible);
    }


    final List<FieldMeta<?, ?>> fieldList;

    final List<_Expression<?>> valueExpList;

    final List<_Predicate> predicateList;


    private SingleUpdateContext(_SingleUpdate update, byte database, Dialect dialect, Visible visible) {
        super(update.table(), update.tableAlias(), database, dialect, visible);
        this.fieldList = update.fieldList();
        this.valueExpList = update.valueExpList();
        this.predicateList = update.predicateList();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        if (!this.safeTableAlias.equals(tableAlias) || fieldMeta.tableMeta() != this.table) {
            throw _Exceptions.unknownColumn(tableAlias, fieldMeta);
        }
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(this.dialect.quoteIfNeed(tableAlias))
                .append(Constant.POINT)
                .append(this.dialect.quoteIfNeed(fieldMeta.columnName()));
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.table) {
            throw _Exceptions.unknownColumn(null, fieldMeta);
        }
        final String tableAlias = this.safeTableAlias;
        final StringBuilder sqlBuilder = this.sqlBuilder.append(Constant.SPACE);

        if (tableAlias != null) {
            sqlBuilder.append(this.dialect.quoteIfNeed(tableAlias))
                    .append(Constant.POINT);
        }
        sqlBuilder.append(this.dialect.quoteIfNeed(fieldMeta.columnName()));
    }


    @Override
    public Stmt build() {
        return Stmts.simple(this.sqlBuilder.toString(), this.paramList);
    }


}
