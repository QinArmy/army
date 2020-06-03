package io.army.criteria.impl.inner;

import io.army.criteria.SubQuery;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;

import java.util.List;

@DeveloperForbid
public interface InnerStandardChildSubQueryInsert extends InnerStandardSubQueryInsert {

    @Override
    ChildTableMeta<?> tableMeta();

    List<FieldMeta<?, ?>> parentFieldList();

    SubQuery parentSubQuery();
}
