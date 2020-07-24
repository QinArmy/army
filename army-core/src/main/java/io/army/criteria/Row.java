package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.List;

public interface Row {

    <T extends IDomain> List<FieldMeta<T, ?>> columnList();

    @Override
    String toString();
}
