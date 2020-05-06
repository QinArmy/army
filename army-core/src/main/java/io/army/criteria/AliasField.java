package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldExp;

public interface AliasField<T extends IDomain, F> extends FieldExp<T, F> {

    String tableAlias();

}
