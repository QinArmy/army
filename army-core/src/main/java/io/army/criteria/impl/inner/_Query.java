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
 *
 * @since 0.6.0
 */
public interface _Query extends Query, _PartRowSet, _RowSet._SelectItemListSpec, _Statement._WithClauseSpec {

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
