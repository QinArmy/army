package io.army.criteria.impl;

import io.army.criteria.PrimaryValueEqualPredicate;
import io.army.criteria.ValueExpression;
import io.army.meta.PrimaryFieldMeta;

public abstract class Predicates {

    protected Predicates() {
        throw new UnsupportedOperationException();
    }

    public static PrimaryValueEqualPredicate primaryValueEquals(PrimaryFieldMeta<?, ?> primary, Object value) {
        return DualPredicate.buildPrimaryValueEqual(primary, SQLS.param(value, primary.mappingMeta()));
    }

    public static PrimaryValueEqualPredicate primaryValueEquals(PrimaryFieldMeta<?, ?> primary
            , ValueExpression<?> valueExp) {
        return DualPredicate.buildPrimaryValueEqual(primary, valueExp);
    }

}
