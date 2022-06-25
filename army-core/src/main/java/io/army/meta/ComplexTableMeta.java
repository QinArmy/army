package io.army.meta;

import io.army.domain.IDomain;

public interface ComplexTableMeta<P extends IDomain, T extends IDomain> extends ChildTableMeta<T> {

    @Override
    ParentTableMeta<P> parentMeta();

}
