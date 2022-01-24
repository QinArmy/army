package io.army.dialect;

import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;
import io.army.criteria.impl.inner._Predicate;
import io.army.meta.ChildTableMeta;

import java.util.List;

public interface _ChildUpdateContext extends _UpdateContext {

    ChildTableMeta<?> childTable();

    String childTableAlias();

    List<? extends SetLeftItem> childTargetParts();

    List<? extends SetRightItem> childValueParts();

    List<_Predicate> childPredicates();


}
