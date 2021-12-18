package io.army.dialect;


import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ChildSetClause}</li>
 *     </ul>
 *     If the implementation of {@link _UpdateContext} isn't {@link _MultiUpdateContext} instance,
 *     then this interface is implemented by the implementation of {@link _UpdateContext}.
 *     If If the implementation of {@link _UpdateContext} is {@link _MultiUpdateContext} instance,
 *     then this interface is implemented by not {@link _UpdateContext} type.
 * </p>
 */
public interface _SetClause {

    TableMeta<?> table();

    String tableAlias();

    boolean hasSelfJoint();

    char[] safeTableAlias();

    List<? extends SetTargetPart> targetParts();

    List<? extends SetValuePart> valueParts();


}
