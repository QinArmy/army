package io.army.boot;

import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.meta.FieldMeta;


abstract class ExecutorUtils {

    ExecutorUtils() {
        throw new UnsupportedOperationException();
    }


    static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec, FieldMeta<?, ?> fieldMeta
            , Object value) {
        return new FieldCodecReturnException("FieldCodec[%s] return value[%s] must error,FieldMeta[%s],"
                , fieldCodec, value, fieldMeta);
    }


}
