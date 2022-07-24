package io.army.meta;



import io.army.lang.Nullable;

public interface IndexFieldMeta<T> extends FieldMeta<T> {

    IndexMeta<T> indexMeta();

    @Nullable
    Boolean fieldAsc();

}
