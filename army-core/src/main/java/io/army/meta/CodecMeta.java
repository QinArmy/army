package io.army.meta;

import io.army.codec.FieldCodec;

import javax.annotation.Nullable;

public interface CodecMeta extends Meta {

    Class<? extends FieldCodec> codecType();

    @Nullable
    String versionPropName();
}
