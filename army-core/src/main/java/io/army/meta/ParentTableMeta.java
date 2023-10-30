package io.army.meta;


import io.army.struct.CodeEnum;

import javax.annotation.Nonnull;

public interface ParentTableMeta<T> extends SingleTableMeta<T> {


    @Nonnull
    FieldMeta<T> discriminator();

    @Nonnull
    CodeEnum discriminatorValue();


}
