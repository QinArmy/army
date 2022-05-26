package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Assert;

final class ExpressionSelection implements _Selection {

    private final _Expression expression;

    private final String alias;

    ExpressionSelection(_Expression expression, String alias) {
        _Assert.assertHasText(alias, "alias required for Selection");
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public ParamMeta paramMeta() {
        ParamMeta paramMeta = this.expression.paramMeta();
        if (paramMeta instanceof TableField) {
            // ExpressionSelection couldn't return io.army.criteria.TableField ,avoid to statement executor
            // decode selection .
            paramMeta = paramMeta.mappingType();
        }
        return paramMeta;
    }

    @Override
    public UpdateMode updateMode() {
        //expression selection couldn't be updated.
        return UpdateMode.IMMUTABLE;
    }

    @Override
    public void appendSelection(final _SqlContext context) {
        this.expression.appendSql(context);
        final StringBuilder builder;
        builder = context.sqlBuilder()
                .append(_Constant.SPACE_AS_SPACE);

        context.dialect()
                .quoteIfNeed(this.alias, builder);
    }


    @Override
    public String toString() {
        return String.format(" %s AS %s", this.expression, this.alias);
    }
}
