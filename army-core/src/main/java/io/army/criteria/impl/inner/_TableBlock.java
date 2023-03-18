package io.army.criteria.impl.inner;

import io.army.criteria.SQLWords;
import io.army.criteria.TabularItem;
import io.army.criteria.impl._JoinType;
import io.army.lang.Nullable;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ModifierTableBlock}</li>
 *         <li>{@link _DerivedBlock}</li>
 *         <li>{@link _NestedBock}</li>
 *         <li>{@link _CteBlock}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _TableBlock {

    _JoinType jointType();

    TabularItem tableItem();

    String alias();

    List<_Predicate> onClauseList();

    interface _ModifierTableBlockSpec {
        @Nullable
        SQLWords modifier();
    }

}
