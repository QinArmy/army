package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldExp;
import io.army.meta.FieldMeta;

public interface AliasFieldExp<T extends IDomain, F> extends FieldExp<T, F> {

    String tableAlias();

    FieldMeta<T, F> fieldMeta();
}
