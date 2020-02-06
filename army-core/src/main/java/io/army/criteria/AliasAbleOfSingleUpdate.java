package io.army.criteria;

import io.army.domain.IDomain;

public interface AliasAbleOfSingleUpdate<T extends IDomain> extends SetAbleOfSingleUpdate<T> {

    SetAbleOfSingleUpdate<T>  as(String tableAlias);
}
