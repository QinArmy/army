package io.army.dialect;


import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.meta.SingleTableMeta;

import java.util.List;


/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ChildUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
 * </p>
 */
public interface _UpdateContext extends _DmlContext {


    SingleTableMeta<?> table();

    String safeTableAlias();

    List<? extends SetTargetPart> targetParts();

    List<? extends SetValuePart> valueParts();

    List<_Predicate> predicates();


}
