package io.army.dialect.mysql;

import io.army.criteria.GenericField;
import io.army.dialect.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.Objects;

abstract class MysqlDml extends AbstractDm {

    MysqlDml(Dialect dialect) {
        super(dialect);
    }


    @Override
    protected final Stmt standardChildUpdateContext(final _SingleUpdateContext context) {
        final _SetClause childSetClause = context.childSetClause();
        Objects.requireNonNull(childSetClause);

        final Dialect dialect = context.dialect();
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childSetClause.table();
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) context.table();
        final String safeParentTableAlias = context.safeTableAlias();
        final String safeChildTableAlias = childSetClause.safeTableAlias();

        final StringBuilder sqlBuilder = context.sqlBuilder();

        // 1. UPDATE clause
        sqlBuilder.append(UPDATE);
        sqlBuilder.append(Constant.SPACE);
        final byte tableIndex = context.tableIndex();
        final String tableSuffix = context.tableSuffix();
        if (tableIndex == 0) {
            sqlBuilder.append(dialect.quoteIfNeed(childTable.tableName()));
        } else {
            sqlBuilder.append(dialect.quoteIfNeed(childTable.tableName() + tableSuffix));
        }
        sqlBuilder.append(AS_WORD)
                .append(Constant.SPACE)
                .append(safeChildTableAlias);

        //2. join clause
        sqlBuilder.append(JOIN_WORD)
                .append(Constant.SPACE);

        if (tableIndex == 0) {
            sqlBuilder.append(dialect.quoteIfNeed(parentTable.tableName()));
        } else {
            sqlBuilder.append(dialect.quoteIfNeed(parentTable.tableName() + tableSuffix));
        }
        sqlBuilder.append(AS_WORD)
                .append(Constant.SPACE)
                .append(safeParentTableAlias);

        final List<GenericField<?, ?>> childConditionFields, parentConditionFields;
        //3. set clause
        childConditionFields = this.setClause(childSetClause, context);
        parentConditionFields = this.setClause(context, context);

        //4. where clause
        this.dmlWhereClause(context);

        //4.1 append discriminator
        this.discriminator(parentTable, safeParentTableAlias, context);

        //4.2 append visible
        if (parentTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(parentTable, safeParentTableAlias, context);
        }
        //4.3 append child condition update fields
        if (childConditionFields.size() > 0) {
            this.conditionUpdate(safeChildTableAlias, childConditionFields, context);
        }
        //4.4 append parent condition update fields
        if (parentConditionFields.size() > 0) {
            this.conditionUpdate(safeParentTableAlias, parentConditionFields, context);
        }
        return context.build();
    }


}
