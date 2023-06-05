package io.army.criteria.impl.inner;

import io.army.criteria.GroupByItem;
import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.criteria.dialect.Hint;
import io.army.lang.Nullable;

import java.util.List;

/**
 * <p>
 * This interface is inner interface of {@link Query}.
 * </p>
 *
 * @since 1.0
 */
public interface _Query extends Query, _PartRowSet, _RowSet._SelectItemListSpec {

    List<Hint> hintList();

    List<? extends SQLWords> modifierList();


    List<_TabularBlock> tableBlockList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> wherePredicateList();

    /**
     * @return a unmodifiable list
     */
    List<? extends GroupByItem> groupByList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> havingList();


    interface _DistinctOnClauseSpec {

        List<_Expression> distinctOnExpressions();
    }


    interface _WindowClauseSpec {

        List<_Window> windowList();
    }

    interface _LockBlock {


        SQLWords lockStrength();

        List<String> lockTableAliasList();

        @Nullable
        SQLWords lockWaitOption();


    }


}
