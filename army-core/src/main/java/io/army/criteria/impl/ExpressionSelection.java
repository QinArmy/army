package io.army.criteria.impl;

import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.util.Assert;

final class ExpressionSelection implements _Selection {

    private final _Expression<?> expression;

    private final String alias;

    ExpressionSelection(_Expression<?> expression, String alias) {
        Assert.hasText(alias, "alias required for Selection");
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    @Override
    public final MappingType mappingMeta() {
        return this.expression.mappingMeta();
    }

    @Override
    public final void appendSql(_SqlContext context) {
        this.expression.appendSql(context);
        context.sqlBuilder()
                .append(" AS ")
                .append(context.dialect().quoteIfNeed(this.alias));
    }

    @Override
    public final void appendSortPart(_SqlContext context) {
        context.appendIdentifier(this.alias);
    }

    @Override
    public final String toString() {
        return this.expression.toString() + " AS " + this.alias;
    }
}
