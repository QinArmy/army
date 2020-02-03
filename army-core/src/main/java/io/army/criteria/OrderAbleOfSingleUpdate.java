package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

public interface OrderAbleOfSingleUpdate<T extends IDomain> extends LimitAbleOfSingleUpdate {

   <F> OrderAscAbleOfSingleUpdate<T> orderBy(FieldMeta<T,F> orderField);
}
