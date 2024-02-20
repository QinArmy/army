package io.army.dialect;

import io.army.meta.TypeMeta;

import javax.annotation.Nullable;


@FunctionalInterface
public interface LiteralBinder {

    void bind(TypeMeta typeMeta, @Nullable Object value, boolean cast, StringBuilder sqlBuilder);


}
