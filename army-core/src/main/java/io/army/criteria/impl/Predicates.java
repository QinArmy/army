package io.army.criteria.impl;

import io.army.criteria.PrimaryValueEqualPredicate;
import io.army.meta.PrimaryFieldMeta;

public abstract class Predicates {

    protected Predicates() {
        throw new UnsupportedOperationException();
    }

    public static PrimaryValueEqualPredicate primaryValueEquals(PrimaryFieldMeta<?, ?> primary, Object value) {
       throw new UnsupportedOperationException();
    }

    public static PrimaryValueEqualPredicate primaryValueEquals(PrimaryFieldMeta<?, ?> primary
            , ValueExpression<?> valueExp) {
        throw new UnsupportedOperationException();
    }

}
