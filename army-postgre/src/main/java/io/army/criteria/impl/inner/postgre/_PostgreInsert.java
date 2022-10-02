package io.army.criteria.impl.inner.postgre;

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


    interface _ConflictActionClauseResult extends _Insert._ConflictActionClauseSpec
            , _Insert._ConflictActionPredicateClauseSpec {

        @Nullable
        String constraintName();

        List<_ConflictTargetItem> conflictTargetItemList();


        List<_Predicate> indexPredicateList();

        boolean isDoNothing();

    }

    interface _PostgreDomainInsert extends _Insert._DomainInsert, _PostgreInsert {


    }

    interface _PostgreChildDomainInsert extends _Insert._ChildDomainInsert, _PostgreDomainInsert {

        @Override
        _PostgreDomainInsert parentStmt();

    }

    interface _PostgreValueInsert extends _Insert._ValuesInsert, _PostgreInsert {


    }

    interface _PostgreChildValueInsert extends _Insert._ChildValuesInsert, _PostgreValueInsert {

        @Override
        _PostgreValueInsert parentStmt();

    }


    interface _PostgreQueryInsert extends _Insert._QueryInsert, _PostgreInsert {

    }

    interface _PostgreChildQueryInsert extends _Insert._ChildQueryInsert, _PostgreQueryInsert {

        @Override
        _PostgreQueryInsert parentStmt();

    }


}
