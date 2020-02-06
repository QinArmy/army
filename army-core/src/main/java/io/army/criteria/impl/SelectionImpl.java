package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;

/**
 * created  on 2019-02-22.
 */
final class SelectionImpl implements Selection {

    private final Expression<?> expression;

    private final String alias;

    SelectionImpl(Expression<?> expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public String alias() {
        return alias;
    }

    @Override
    public Expression<?> expression() {
        return expression;
    }

}
