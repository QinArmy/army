package io.army.codec;

import io.army.meta.FieldMeta;

import java.util.Set;

/**
 * see {@code io.army.boot.InsertSQLExecutor} and {@code } implementation.
 */
public interface FieldCodec {

    Set<FieldMeta<?, ?>> fieldMetaSet();

    Object encode(FieldMeta<?, ?> fieldMeta, Object nonNullFieldValue, CodecContext context) throws FieldCodecException;

    Object decode(FieldMeta<?, ?> fieldMeta, Object nonNullValueFromDB, CodecContext context) throws FieldCodecException;

}
