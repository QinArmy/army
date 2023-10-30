package io.army.meta;



import javax.annotation.Nullable;

public interface IndexFieldMeta<T> extends FieldMeta<T> {

    IndexMeta<T> indexMeta();

    @Nullable
    Boolean fieldAsc();

}
