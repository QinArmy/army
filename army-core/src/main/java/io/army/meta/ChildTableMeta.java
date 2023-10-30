package io.army.meta;


import io.army.struct.CodeEnum;

import javax.annotation.Nonnull;

public interface ChildTableMeta<T> extends TableMeta<T> {

    @Nonnull
    @Override
    FieldMeta<? super T> discriminator();

    ParentTableMeta<?> parentMeta();

    @Nonnull
    CodeEnum discriminatorValue();


}
