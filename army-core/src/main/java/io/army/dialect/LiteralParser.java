package io.army.dialect;

import io.army.meta.TypeMeta;

import io.army.lang.Nullable;


@FunctionalInterface
public interface LiteralParser {


    void parse(TypeMeta typeMeta, @Nullable Object value, boolean typeName, StringBuilder sqlBuilder);


}
