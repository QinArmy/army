package io.army.dialect.mysql;


import io.army.criteria.GenericField;
import io.army.dialect.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;

import java.util.List;
import java.util.Set;

/**
 * this class is a  {@link _Dialect} implementation then abstract base class of all MySQL 5.7 Dialect
 */
abstract class MySQL57 extends MySQLDialect {

    static MySQL57 create(DialectEnvironment environment) {
        return new MySQL57StandardDialect(environment);
    }


    MySQL57(DialectEnvironment environment) {
        super(environment);

    }

    /*################################## blow interfaces method ##################################*/


    @Override
    public final boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public final boolean hasRowKeywords() {
        return true;
    }

    /*####################################### below AbstractDialect template  method #################################*/

    @Override
    protected final Set<String> createKeyWordSet() {
        return MySQLDialectUtils.create57KeywordsSet();
    }


    /**
     * <p>
     * MySQL {@link _Dialect#multiTableUpdateChild()} always return true
     * ,so this method always use multi-table syntax update child table.
     * </p>
     *
     * @see _Dialect#multiTableUpdateChild()
     */
    @Override
    protected final SimpleStmt standardChildUpdate(final _SingleUpdateContext context) {
        assert !context.multiTableUpdateChild();

        final _SetBlock childSetClause = context.childBlock();
        assert childSetClause != null;

        final _Dialect dialect = context.dialect();
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childSetClause.table();
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) context.table();
        final String safeParentTableAlias = context.safeTableAlias();
        final String safeChildTableAlias = childSetClause.safeTableAlias();

        final StringBuilder sqlBuilder = context.sqlBuilder();

        // 1. UPDATE clause
        sqlBuilder.append(Constant.UPDATE)
                .append(Constant.SPACE)
                // append child table name
                .append(dialect.quoteIfNeed(childTable.tableName()));

        sqlBuilder.append(Constant.SPACE)
                .append(Constant.AS)
                .append(Constant.SPACE)
                .append(safeChildTableAlias);

        //2. join clause
        sqlBuilder.append(Constant.SPACE)
                .append(Constant.JOIN)
                .append(Constant.SPACE)
                // append parent table name
                .append(dialect.quoteIfNeed(parentTable.tableName()))
                .append(Constant.SPACE)
                .append(Constant.AS)
                .append(Constant.SPACE)
                .append(safeParentTableAlias);

        //2.1 on clause
        sqlBuilder.append(Constant.SPACE)
                .append(Constant.ON)
                .append(Constant.SPACE)
                .append(safeChildTableAlias)
                .append(Constant.POINT)
                .append(_MetaBridge.ID)
                .append(Constant.SPACE_EQUAL)
                .append(Constant.SPACE)
                .append(safeParentTableAlias)
                .append(Constant.POINT)
                .append(_MetaBridge.ID);

        final List<GenericField<?, ?>> childConditionFields, parentConditionFields;
        //3. set clause
        childConditionFields = this.setClause(childSetClause, context);
        parentConditionFields = this.setClause(context, context);

        //4. where clause
        this.dmlWhereClause(context);

        //4.1 append discriminator for child
        this.discriminator(childTable, safeParentTableAlias, context);

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


    private static final class MySQL57StandardDialect extends MySQL57 {

        private MySQL57StandardDialect(DialectEnvironment environment) {
            super(environment);
        }

    }


}
