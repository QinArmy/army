package io.army.dialect;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValueItem;
import io.army.criteria.impl.inner._Predicate;
import io.army.meta.ChildTableMeta;

import java.util.List;

public interface _ChildUpdateContext extends _UpdateContext {

    ChildTableMeta<?> childTable();

    String childTableAlias();

    List<? extends SetTargetPart> childTargetParts();

    List<? extends SetValueItem> childValueParts();

    List<_Predicate> childPredicates();


}
