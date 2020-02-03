package io.army.meta;

import io.army.domain.IDomain;

public interface NumberIndexFieldMeta<T extends IDomain, F extends Number>
        extends NumberFieldMeta<T, F>, IndexFieldMeta<T, F> {

}
