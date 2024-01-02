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

import io.army.criteria.ItemPair;
import io.army.criteria.SqlField;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;

import java.util.List;

public interface _ItemPair extends ItemPair {


    /**
     * @param sqlBuilder must be {@link _SqlContext#sqlBuilder()} of context . For reducing method invoking.
     */
    void appendItemPair(StringBuilder sqlBuilder, _SetClauseContext context);


    interface _FieldItemPair extends _ItemPair {

        SqlField field();

        _Expression value();

    }

    interface _RowItemPair extends _ItemPair {

        List<? extends SqlField> rowFieldList();

    }


}
