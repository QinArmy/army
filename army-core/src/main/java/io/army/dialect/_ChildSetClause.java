package io.army.dialect;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.meta.ChildTableMeta;

import java.util.List;

@Deprecated
public interface _ChildSetClause extends _SetClause {

    ChildTableMeta<?> childTable();

    String childTableAlias();

    String childSafeTableAlias();

    List<? extends SetTargetPart> childTargetParts();

    List<? extends SetValuePart> childValueParts();


}
