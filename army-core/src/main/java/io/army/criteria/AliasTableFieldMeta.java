package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

public interface AliasTableFieldMeta<T extends IDomain,F> extends FieldMeta<T,F> {

    String tableAlias();
}
