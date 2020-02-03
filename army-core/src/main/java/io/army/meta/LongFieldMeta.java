package io.army.meta;

import io.army.criteria.LongExpression;
import io.army.criteria.NumberExpression;
import io.army.domain.IDomain;

public interface LongFieldMeta<T extends IDomain> extends NumberFieldMeta<T,Long> , LongExpression {

}
