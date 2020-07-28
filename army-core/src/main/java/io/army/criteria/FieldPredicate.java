package io.army.criteria;

import io.army.meta.FieldMeta;
import io.army.meta.GenericField;

/**
 * this interface representing a {@link IPredicate} than at least contains a {@link GenericField}.
 *
 */
public interface FieldPredicate extends IPredicate,FieldExpression<Boolean> {

    FieldMeta<?, ?> fieldMeta();

    @Override
    void appendSQL(SQLContext context);

    void appendPredicate(SQLContext context);
}
