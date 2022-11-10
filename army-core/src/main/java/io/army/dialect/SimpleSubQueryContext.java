package io.army.dialect;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

final class SimpleSubQueryContext extends MultiTableQueryContext implements _SubQueryContext {


    static SimpleSubQueryContext create(final _SqlContext outerCtx,final SubQuery subQuery) {
        final StatementContext outerContext = (StatementContext) outerCtx;
         final ArmyParser parser = outerContext.parser;

        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) subQuery).tableBlockList(), parser, outerContext.visible);
        return new SimpleSubQueryContext( outerContext, subQuery, tableContext);
    }

    private final StatementContext outerContext;

    private SimpleSubQueryContext(StatementContext outerContext, SubQuery subQuery, TableContext tableContext) {
        super(outerContext, subQuery, tableContext, outerContext.parser, outerContext.visible);
        this.outerContext = outerContext;
    }


    @Override
    public void appendThisField(final String tableAlias, final FieldMeta<?> field) {
        if (this.multiTableContext.aliasToTable.get(tableAlias) != field.tableMeta()) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.multiTableContext.appendSafeField(tableAlias, field);
    }

    @Override
    public void appendThisField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final String safeTableAlias;
        safeTableAlias = this.multiTableContext.tableToSafeAlias.get(fieldTable);
        if (safeTableAlias != null) {
            final StringBuilder sqlBuilder = this.sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (this.multiTableContext.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else {
            throw _Exceptions.unknownColumn(null, field);
        }
    }


    @Override
    void appendOuterField(final @Nullable String tableAlias, final FieldMeta<?> field) {
        final StatementContext outerContext = this.outerContext;
        if (outerContext instanceof _ParenRowSetContext) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        } else if (outerContext instanceof _SubQueryContext) {
            if (tableAlias == null) {
                ((_SubQueryContext) outerContext).appendThisField(field);
            } else {
                ((_SubQueryContext) outerContext).appendThisField(tableAlias, field);
            }
        } else if (tableAlias == null) {
            outerContext.appendField(field);
        } else {
            outerContext.appendField(tableAlias, field);
        }

    }


}
