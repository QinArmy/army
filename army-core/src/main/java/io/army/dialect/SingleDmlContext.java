package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util._Exceptions;

abstract class SingleDmlContext extends SingleTableDmlContext {


    SingleDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
    }

    SingleDmlContext(_SingleDml stmt, SingleTableDmlContext parentContext) {
        super(stmt, parentContext);
    }


    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (this.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        if (field.tableMeta() != this.targetTable) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(_Constant.SPACE);
        if (this.supportAlias) {
            sqlBuilder.append(this.safeTableAlias);
        } else {
            this.parser.safeObjectName(this.targetTable, sqlBuilder);
        }
        sqlBuilder.append(_Constant.POINT);
        this.parser.safeObjectName(field, sqlBuilder);

    }


}
