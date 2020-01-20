package io.army.meta;


import io.army.domain.IDomain;
import org.springframework.lang.Nullable;

public interface IndexFieldMeta<T extends IDomain, F> extends FieldMeta<T, F> {

    IndexMeta<T> indexMeta();

    @Nullable
    Boolean fieldAsc();

}
