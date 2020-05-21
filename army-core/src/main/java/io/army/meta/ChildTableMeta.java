package io.army.meta;

import io.army.domain.IDomain;
import io.army.lang.NonNull;
import io.army.struct.CodeEnum;

public interface ChildTableMeta<T extends IDomain> extends TableMeta<T> {

    @NonNull
    @Override
    <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator();

    @NonNull
    ParentTableMeta<? super T> parentMeta();

}
