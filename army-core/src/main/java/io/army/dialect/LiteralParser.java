package io.army.dialect;

import io.army.env.EscapeMode;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;


@FunctionalInterface
public interface LiteralParser {

    boolean parse(TypeMeta typeMeta, @Nullable Object value, EscapeMode mode, StringBuilder sqlBuilder);


}
