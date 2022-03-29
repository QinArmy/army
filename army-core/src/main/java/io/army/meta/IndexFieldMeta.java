package io.army.meta;


import io.army.domain.IDomain;
import io.army.lang.Nullable;

public interface IndexFieldMeta<T extends IDomain> extends FieldMeta<T> {

    IndexMeta<T> indexMeta();

    @Nullable
    Boolean fieldAsc();

}
