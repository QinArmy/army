package io.army.criteria;


import io.army.criteria.impl.inner._Predicate;
import io.army.meta.PrimaryFieldMeta;

/**
 *
 */
public interface PrimaryValueEqualPredicate extends FieldValueEqualPredicate, _Predicate {

    @Override
    PrimaryFieldMeta<?, ?> fieldMeta();

}
