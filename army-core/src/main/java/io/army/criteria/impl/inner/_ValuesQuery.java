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

import io.army.criteria.ValuesQuery;

import java.util.List;


/**
 * <p>
 * This interface is inner interface of {@link ValuesQuery}.
 *
 * @since 0.6.0
 */
public interface _ValuesQuery extends ValuesQuery, _PartRowSet, _RowSet._SelectItemListSpec {

    @Override
    List<_Selection> selectItemList();

    List<List<Object>> rowList();


}
