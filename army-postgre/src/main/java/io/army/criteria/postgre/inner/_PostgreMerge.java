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

package io.army.criteria.postgre.inner;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Merge;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;
import java.util.List;

public interface _PostgreMerge extends _Merge, _Statement._WithClauseSpec {

    @Nullable
    SQLWords targetModifier();


    List<_WhenPair> whenPairList();




    interface _WhenPair extends _WherePredicateListSpec {

        boolean isDoNothing();

    }


    interface _WhenMatchedPair extends _WhenPair {

        List<_ItemPair> updateItemPairList();

        boolean isDelete();

    }

    interface _WhenNotMatchedPair extends _WhenPair {

        @Nullable
        _Insert insertStmt();

    }


}
