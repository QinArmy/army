/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;
import java.util.List;

public interface _PostgreInsert extends _Insert, _Insert._SupportConflictClauseSpec, _Insert._SupportWithClauseInsert,
        _Statement._ReturningListSpec {

    @Nullable
    SQLWords overridingValueWords();


    @Nullable
    _ConflictActionClauseResult getConflictActionResult();


    interface _ConflictActionClauseResult extends _Insert._ConflictActionClauseSpec
            , _Insert._ConflictActionPredicateClauseSpec {

        @Nullable
        String constraintName();

        List<_ConflictTargetItem> conflictTargetItemList();


        List<_Predicate> indexPredicateList();

        boolean isIgnorableConflict();

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

    interface _PostgreParentQueryInsert extends _PostgreQueryInsert, _ParentQueryInsert {

    }

    interface _PostgreChildQueryInsert extends _Insert._ChildQueryInsert, _PostgreQueryInsert {

        @Override
        _PostgreParentQueryInsert parentStmt();

    }


}
