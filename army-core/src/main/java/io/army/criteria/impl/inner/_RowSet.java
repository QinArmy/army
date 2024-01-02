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

import io.army.criteria.RowSet;

import java.util.List;

/**
 * <p>
 * This interface is inner interface of {@link RowSet}.This interface is base interface of below:
 * <ul>
 *     <li>{@link _ParensRowSet}</li>
 *     <li>{@link _PartRowSet}</li>
 *     <li>{@link _UnionRowSet}</li>
 *     <li>{@link _PrimaryRowSet}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface _RowSet extends _Statement, RowSet {

    int selectionSize();


    interface _SelectItemListSpec {

        List<? extends _SelectItem> selectItemList();
    }


}
