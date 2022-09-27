package io.army.meta;


import io.army.lang.NonNull;
import io.army.struct.CodeEnum;

public interface ChildTableMeta<T> extends TableMeta<T> {

    @NonNull
    @Override
    FieldMeta<? super T> discriminator();

    ParentTableMeta<?> parentMeta();

    @NonNull
    CodeEnum discriminatorValue();


}
