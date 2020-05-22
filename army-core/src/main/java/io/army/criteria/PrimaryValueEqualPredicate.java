package io.army.criteria;


import io.army.meta.PrimaryFieldMeta;

/**
 *
 */
public interface PrimaryValueEqualPredicate extends FieldValuePredicate {

    @Override
    PrimaryFieldMeta<?, ?> fieldExp();

}
