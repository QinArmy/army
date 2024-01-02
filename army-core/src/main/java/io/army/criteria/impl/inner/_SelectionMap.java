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

import io.army.criteria.Selection;
import io.army.dialect._SqlContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>
 * Package interface,this interface is base interface of below:
 * <ul>
 *     <li>{@link _AliasDerivedBlock}</li>
 *     <li>{@link _DerivedTable}</li>
 *     <li>{@link  _Cte}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface _SelectionMap {

    /**
     * <p>
     * Note,the {@link Selection} couldn't be rendered. so couldn't invoke below method:
     *     <ul>
     *         <li>{@link io.army.criteria.impl.inner._SelfDescribed#appendSql(StringBuilder, _SqlContext)}</li>
     *         <li>{@link _Selection#appendSelectItem(StringBuilder, _SqlContext)}</li>
     *     </ul>
     *
     *
     * @return the {@link Selection} that couldn't be rendered.
     */
    @Nullable
    Selection refSelection(String name);

    /**
     * <p>
     * Note,any {@link Selection} of list couldn't be rendered. so couldn't invoke below method:
     *     <ul>
     *         <li>{@link io.army.criteria.impl.inner._SelfDescribed#appendSql(StringBuilder, _SqlContext)}</li>
     *         <li>{@link _Selection#appendSelectItem(StringBuilder, _SqlContext)}</li>
     *     </ul>
     *
     *
     * @return a unmodified list, the list of {@link Selection} that couldn't be rendered.
     */
    List<? extends Selection> refAllSelection();


}
