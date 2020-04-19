package io.army.meta;

import io.army.domain.IDomain;
import io.army.struct.CodeEnum;

public interface ParentTableMeta<T extends IDomain> extends TableMeta<T> {

    <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator();


}
