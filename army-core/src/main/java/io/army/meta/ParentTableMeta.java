package io.army.meta;

import io.army.domain.IDomain;
import io.army.lang.NonNull;

public interface ParentTableMeta<T extends IDomain> extends SingleTableMeta<T> {


    @NonNull
    FieldMeta<T> discriminator();


}
