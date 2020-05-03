package io.army.codec;

import io.army.beans.DomainReadonlyWrapper;
import io.army.meta.FieldMeta;

import java.util.Set;

/**
 * see {@code io.army.boot.InsertSQLExecutor} and {@code } implementation.
 */
public interface FieldCodec {

    Set<FieldMeta<?, ?>> fieldMetaSet();

    Object encode(FieldMeta<?, ?> fieldMeta, Object fieldValue, DomainReadonlyWrapper readonlyWrapper);

    Object decode(FieldMeta<?, ?> fieldMeta, Object valueFromDB, DomainReadonlyWrapper readonlyWrapper);

}
