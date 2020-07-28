package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.GenericField;

public interface LogicalField<T extends IDomain, F> extends GenericField<T, F> {

    String tableAlias();

}
