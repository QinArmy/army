package io.army.criteria.impl.inner.postgre;

import io.army.criteria.ItemPair;
import io.army.criteria.SelectItem;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;

import java.util.List;

public interface _PostgreInsert extends _Insert {


    List<? extends SelectItem> returningList();

    @Nullable
    _ConflictActionClauseResult getConflictActionResult();


    interface _ConflictActionClauseResult {

        @Nullable
        String constraintName();

        List<_ConflictTargetItem> conflictTargetItemList();


        List<_Predicate> indexPredicateList();

        boolean isDoNothing();

        List<ItemPair> updateSetClauseList();

        List<_Predicate> updateSetPredicateList();
    }


}
