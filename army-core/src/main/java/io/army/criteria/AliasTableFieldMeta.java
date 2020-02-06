package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

public interface AliasTableFieldMeta<T extends IDomain, F> extends FieldMeta<T, F> {

    FieldMeta<T, F> fieldMeta();

    String tableAlias();

    @Override
    default AliasTableFieldMeta<T, F> table(String tableAlias) {
        throw new UnsupportedOperationException("only alias once");
    }
}
