package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldExpression;

public interface LogicalField<T extends IDomain, F> extends FieldExpression<T, F> {

    String tableAlias();

}
