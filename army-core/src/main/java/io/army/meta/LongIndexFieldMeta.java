package io.army.meta;

import io.army.domain.IDomain;

public interface LongIndexFieldMeta<T extends IDomain>
        extends LongFieldMeta<T>, IndexFieldMeta<T, Long> {

}
