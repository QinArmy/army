package io.army.criteria;

import io.army.meta.FieldMeta;

public interface FieldPredicate extends SpecialPredicate {

    FieldMeta<?, ?> fieldExp();

}
