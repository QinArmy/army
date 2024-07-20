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

import io.army.criteria.SQLWords;
import io.army.criteria.TabularItem;
import io.army.criteria.impl._JoinType;
import io.army.lang.Nullable;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ModifierTabularBlock}</li>
 *         <li>{@link _AliasDerivedBlock}</li>
 *         <li>{@link _DoneFuncBlock}</li>
 *     </ul>
 * * @since 0.6.0
 */
public interface _TabularBlock {

    _JoinType jointType();

    TabularItem tableItem();

    String alias();

    List<_Predicate> onClauseList();

    interface _ModifierTableBlockSpec {
        @Nullable
        SQLWords modifier();
    }

}
