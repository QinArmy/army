package io.army.dialect.impl;

import io.army.meta.TypeMeta;

import javax.annotation.Nullable;


@Deprecated
@FunctionalInterface
public interface LiteralParser {


    void parse(TypeMeta typeMeta, @Nullable Object value, boolean typeName, StringBuilder sqlBuilder);


}
