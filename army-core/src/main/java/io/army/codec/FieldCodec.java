package io.army.codec;

import io.army.meta.FieldMeta;

import java.util.Set;

public interface FieldCodec {

    Set<FieldMeta<?, ?>> fieldMetaSet();

    Object encode(FieldMeta<?, ?> fieldMeta, Object fieldValue);

    Object decode(FieldMeta<?, ?> fieldMeta, Object valueFromDB);

}
