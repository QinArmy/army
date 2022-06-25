package io.army.meta;

import io.army.domain.IDomain;
import io.army.lang.NonNull;

public interface ChildTableMeta<T extends IDomain> extends TableMeta<T> {

    @NonNull
    @Override
    FieldMeta<? super T> discriminator();

    ParentTableMeta<?> parentMeta();

    int discriminatorValue();


}
