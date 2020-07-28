package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;

final class DefaultSelection implements Selection {

    private final Expression<?> expression;

    private final String alias;

    DefaultSelection(Expression<?> expression, String alias) {
        Assert.hasText(alias,"alias required for Selection");
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    @Override
    public final MappingMeta mappingMeta() {
        return this.expression.mappingMeta();
    }

    @Override
    public final void appendSQL(SQLContext context) {
        this.expression.appendSQL(context);
        context.sqlBuilder()
                .append(" AS ")
                .append(context.dql().quoteIfNeed(this.alias));
    }

    @Override
    public final void appendSortPart(SQLContext context) {
       context.appendText(this.alias);
    }

    @Override
    public final String toString() {
        return this.expression.toString() + " AS " + this.alias;
    }
}
