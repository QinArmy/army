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

package io.army.schema;


import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * @see SchemaResult
 */
public interface TableResult {

    TableMeta<?> table();

    boolean comment();

    List<FieldMeta<?>> newFieldList();

    List<FieldResult> changeFieldList();

    List<String> newIndexList();

    List<String> changeIndexList();

    static Builder builder() {
        return TableResultImpl.createBuilder();
    }


    interface Builder {

        void table(TableMeta<?> table);

        void appendNewColumn(FieldMeta<?> field);

        void comment(boolean comment);

        void appendFieldResult(FieldResult fieldResult);

        void appendNewIndex(String indexName);

        void appendChangeIndex(String indexName);


        TableResult buildAndClear();

    }


}
