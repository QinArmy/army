package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.meta.mapping.MappingMeta;

final class DefaultSelection implements Selection {

    private final Expression<?> expression;

    private final String alias;

    DefaultSelection(Expression<?> expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public MappingMeta mappingMeta() {
        return this.expression.mappingMeta();
    }

    @Override
    public void appendSQL(SQLContext context) {
        this.expression.appendSQL(context);
        context.sqlBuilder()
                .append(" AS ")
                .append(context.dql().quoteIfNeed(this.alias));
    }

    @Override
    public void appendSortPart(SQLContext context) {
        this.expression.appendSQL(context);
    }

    @Override
    public String toString() {
        return this.expression.toString() + " AS " + this.alias;
    }
}
