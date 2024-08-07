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

import io.army.criteria.Selection;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.lang.Nullable;

public interface _PostgreCte extends _Cte {

    @Nullable
    SQLs.WordMaterialized modifier();

    @Nullable
    _SearchClause searchClause();

    _CycleClause cycleClause();


    interface _SearchClause extends _SelfDescribed {

        Selection searchSeqSelection();


    }

    interface _CycleClause extends _SelfDescribed {

        Selection cycleMarkSelection();


        Selection cyclePathSelection();

    }



}
