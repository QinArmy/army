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
import io.army.dialect.impl._SqlContext;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>
 * This interface representing group consist of multi select list clause items.
 *
 * @since 0.6.0
 */
public interface _SelectionGroup extends _SelectItem {

    String tableAlias();


    /**
     * <p>
     * Note,any element of the list couldn't be rendered. so couldn't invoke below method:
     *     <ul>
     *         <li>{@link io.army.criteria.impl.inner._SelfDescribed#appendSql(StringBuilder, _SqlContext)}</li>
     *         <li>{@link _Selection#appendSelectItem(StringBuilder, _SqlContext)}</li>
     *     </ul>
     *
     *
     * @return element list of this group.
     */
    List<? extends Selection> selectionList();


    interface _TableFieldGroup extends _SelectionGroup {


        boolean isLegalGroup(@Nullable TableMeta<?> table);

    }


}
