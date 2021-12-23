package io.army.criteria;

import io.army.domain.IDomain;

public interface QualifiedField<T extends IDomain, F> extends GenericField<T, F> {

    String tableAlias();

}
