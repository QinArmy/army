package io.army.dialect;

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Query;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

final class SimpleSubQueryContext extends MultiTableContext implements _SimpleQueryContext, _SubQueryContext {


    static SimpleSubQueryContext create(SubQuery subQuery, _SqlContext outerContext) {
        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) subQuery).tableBlockList()
                , (ArmyParser) outerContext.parser(), outerContext.visible());
        return new SimpleSubQueryContext(tableContext, outerContext);
    }

    private final _SqlContext outerContext;

    private SimpleSubQueryContext(TableContext tableContext, _SqlContext outerContext) {
        super(tableContext, (StatementContext) outerContext);
        this.outerContext = outerContext;
    }


    @Override
    public void appendThisField(final String tableAlias, final FieldMeta<?> field) {
        if (this.aliasToTable.get(tableAlias) != field.tableMeta()) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendSafeField(tableAlias, field);
    }

    @Override
    public void appendThisField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final String safeTableAlias;
        safeTableAlias = this.tableToSafeAlias.get(fieldTable);
        if (safeTableAlias != null) {
            final StringBuilder sqlBuilder = this.sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (this.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else {
            throw _Exceptions.unknownColumn(null, field);
        }
    }


    @Override
    void appendOuterField(final String tableAlias, final FieldMeta<?> field) {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext instanceof _UnionQueryContext) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        } else if (outerContext instanceof _SubQueryContext) {
            ((_SubQueryContext) outerContext).appendThisField(tableAlias, field);
        } else {
            outerContext.appendField(field);
        }
    }

    @Override
    void appendOuterField(final FieldMeta<?> field) {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext instanceof _UnionQueryContext) {
            throw _Exceptions.unknownColumn(field);
        } else if (outerContext instanceof _SubQueryContext) {
            ((_SubQueryContext) outerContext).appendThisField(field);
        } else {
            outerContext.appendField(field);
        }
    }


    static UnsupportedOperationException dontSupportBuild() {
        return new UnsupportedOperationException("Sub query context don't support build operation.");
    }

}
