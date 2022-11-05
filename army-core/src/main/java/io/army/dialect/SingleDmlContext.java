package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util._Exceptions;

abstract class SingleDmlContext extends SingleDmlStmtContext {


    SingleDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser0 parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
    }

    SingleDmlContext(_SingleDml stmt, SingleDmlStmtContext parentContext) {
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
        if (field.tableMeta() != this.domainTable) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(_Constant.SPACE);
        if (this.supportAlias) {
            sqlBuilder.append(this.safeTableAlias)
                    .append(_Constant.POINT);
        }
        this.parser.safeObjectName(field, sqlBuilder);

    }



}
