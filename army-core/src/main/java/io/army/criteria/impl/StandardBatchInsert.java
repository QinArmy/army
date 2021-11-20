package io.army.criteria.impl;

import io.army.criteria.impl.inner._StandardBatchInsert;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

final class StandardBatchInsert<T extends IDomain> extends StandardInsert<T>
        implements _StandardBatchInsert {

    static <T extends IDomain> StandardBatchInsert<T> build(TableMeta<T> tableMeta) {
        return new StandardBatchInsert<>(tableMeta);
    }

    private StandardBatchInsert(TableMeta<T> tableMeta) {
        super(tableMeta);
    }


}
