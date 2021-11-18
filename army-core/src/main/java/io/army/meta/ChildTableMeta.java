package io.army.meta;

import io.army.domain.IDomain;

public interface ChildTableMeta<T extends IDomain> extends TableMeta<T> {

    ParentTableMeta<? super T> parentMeta();

    int discriminatorValue();

}
