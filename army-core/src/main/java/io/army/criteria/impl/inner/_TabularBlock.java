package io.army.criteria.impl.inner;

import io.army.criteria.SQLWords;
import io.army.criteria.TabularItem;
import io.army.criteria.impl._JoinType;

import javax.annotation.Nullable;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ModifierTabularBlock}</li>
 *         <li>{@link _AliasDerivedBlock}</li>
 *         <li>{@link _DoneFuncBlock}</li>
 *     </ul>
 * * @since 1.0
 */
public interface _TabularBlock {

    _JoinType jointType();

    TabularItem tableItem();

    String alias();

    List<_Predicate> onClauseList();

    interface _ModifierTableBlockSpec {
        @Nullable
        SQLWords modifier();
    }

}
