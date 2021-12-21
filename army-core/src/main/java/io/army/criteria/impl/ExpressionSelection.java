package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._SortPart;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.util.Assert;

final class ExpressionSelection implements Selection, _SelfDescribed, _SortPart {

    private final _Expression<?> expression;

    private final String alias;

    ExpressionSelection(_Expression<?> expression, String alias) {
        Assert.hasText(alias, "alias required for Selection");
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public MappingType mappingType() {
        return this.expression.mappingType();
    }

    @Override
    public void appendSql(_SqlContext context) {
        this.expression.appendSql(context);
        context.sqlBuilder()
                .append(" AS ")
                .append(context.dialect().quoteIfNeed(this.alias));
    }

    @Override
    public void appendSortPart(_SqlContext context) {
        context.appendIdentifier(this.alias);
    }

    @Override
    public boolean nullable() {
        return this.expression.nullableExp();
    }

    @Override
    public String toString() {
        return this.expression.toString() + " AS " + this.alias;
    }
}
