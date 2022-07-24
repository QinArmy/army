package io.army.meta;


import io.army.lang.NonNull;

public interface ParentTableMeta<T> extends SingleTableMeta<T> {


    @NonNull
    FieldMeta<T> discriminator();


}
