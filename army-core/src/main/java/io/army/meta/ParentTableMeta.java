package io.army.meta;


import io.army.lang.NonNull;
import io.army.struct.CodeEnum;

public interface ParentTableMeta<T> extends SingleTableMeta<T> {


    @NonNull
    FieldMeta<T> discriminator();

    @NonNull
    CodeEnum discriminatorValue();


}
