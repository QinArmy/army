package io.army.criteria.impl.inner;

import io.army.criteria.TabularItem;
import io.army.criteria.impl._JoinType;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>The implementation of a table block in sql from clause</li>
 *         <li>{@link _NoTableBlock}</li>
 *         <li>{@link _JoinType}</li>
 *         <li>{@link _LeftBracketBlock}</li>
 *         <li>{@link _RightBracketBlock}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _TableBlock {

    _JoinType jointType();

    TabularItem tableItem();

    String alias();

    List<_Predicate> predicateList();

}
