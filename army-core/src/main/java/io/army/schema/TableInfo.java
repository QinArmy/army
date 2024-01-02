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

import javax.annotation.Nullable;
import java.util.Map;

public interface TableInfo {

    /**
     * @return lower case table name
     */
    String name();


    @Nullable
    String comment();

    /**
     * @return map, key : column name (lower case),value: {@link ColumnInfo}.
     */
    Map<String, ColumnInfo> columnMap();

    Map<String, IndexInfo> indexMap();


    static Builder builder(boolean reactive, String tableName) {
        return TableInfoImpl.createBuilder(reactive, tableName);
    }


    interface Builder {

        String name();

        Builder type(TableType type);

        Builder comment(@Nullable String comment);

        Builder appendColumn(ColumnInfo column);

        Builder appendIndex(IndexInfo indexInfo);

        TableInfo build();

    }

}
