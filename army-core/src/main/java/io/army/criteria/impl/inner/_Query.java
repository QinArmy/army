package io.army.criteria.impl.inner;

import io.army.criteria.SQLWords;
import io.army.criteria.SortItem;
import io.army.criteria.dialect.Hint;

import java.util.List;

public interface _Query extends _PartRowSet {

    List<Hint> hintList();

    List<? extends SQLWords> modifierList();


    List<_TableBlock> tableBlockList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> wherePredicateList();

    /**
     * @return a unmodifiable list
     */
    List<? extends SortItem> groupByList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> havingList();


}
