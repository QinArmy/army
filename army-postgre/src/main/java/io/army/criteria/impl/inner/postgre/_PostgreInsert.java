package io.army.criteria.impl.inner.postgre;

import io.army.criteria.ItemPair;
import io.army.criteria.SQLWords;
import io.army.criteria.SelectItem;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;

import java.util.List;

public interface _PostgreInsert extends _Insert, _Insert._SupportReturningClauseSpec
        , _Insert._SupportConflictClauseSpec, _Insert._SupportWithClauseInsert {


    @Nullable
    String tableAlias();

    @Nullable
    SQLWords overridingValueWords();


    @Nullable
    _ConflictActionClauseResult getConflictActionResult();

    List<? extends SelectItem> returningList();


    interface _ConflictActionClauseResult {

        @Nullable
        String constraintName();

        List<_ConflictTargetItem> conflictTargetItemList();


        List<_Predicate> indexPredicateList();

        boolean isDoNothing();

        List<ItemPair> updateSetClauseList();

        List<_Predicate> updateSetPredicateList();
    }

    interface _PostgreDomainInsert extends _Insert._DomainInsert, _PostgreInsert {


    }

    interface _PostgreChildDomainInsert extends _Insert._ChildDomainInsert, _PostgreDomainInsert {


    }


}
