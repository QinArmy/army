package io.army.criteria.impl.inner;

import io.army.criteria.Hint;
import io.army.criteria.SQLModifier;
import io.army.criteria.SelectPart;
import io.army.criteria.SortPart;

import java.util.List;

public interface _Query extends _PartQuery {

    List<Hint> hintList();

    List<SQLModifier> modifierList();

    List<? extends SelectPart> selectPartList();

    List<? extends _TableBlock> tableBlockList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();

    /**
     * @return a unmodifiable list
     */
    List<SortPart> groupPartList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> havingList();



}
