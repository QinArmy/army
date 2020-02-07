package io.army.criteria;

import io.army.domain.IDomain;

public interface AliasAbleOfSingleUpdate<T extends IDomain,C1,C2> extends SetAbleOfSingleUpdate<T,C1,C2> {

    SetAbleOfSingleUpdate<T,C1,C2>  as(String tableAlias);


}
