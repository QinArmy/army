package io.army.criteria.impl.inner;

import io.army.criteria.SubQuery;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;

import java.util.List;

public interface _StandardChildSubQueryInsert extends _StandardSubQueryInsert {

    @Override
    ChildTableMeta<?> table();

    List<FieldMeta<?, ?>> parentFieldList();

    SubQuery parentSubQuery();
}
