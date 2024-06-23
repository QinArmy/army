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

import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class SchemaInfoImpl implements SchemaInfo {

    static SchemaInfoImpl create(@Nullable String catalog, @Nullable String schema,
                                 Map<String, TableInfo.Builder> builderMap,
                                 List<Map<String, Object>> userDefinedTypeList) {

        final Map<String, TableInfo> tableMap = _Collections.hashMap(builderMap.size());
        for (TableInfo.Builder builder : builderMap.values()) {
            //table name must lower case
            if (tableMap.putIfAbsent(builder.name().toLowerCase(Locale.ROOT), builder.build()) != null) {
                throw new IllegalArgumentException("builderMap error.");
            }
        }
        return new SchemaInfoImpl(catalog, schema, tableMap, userDefinedTypeList);
    }


    private final String catalog;

    private final String schema;

    private final Map<String, TableInfo> builderMap;


    private final List<Map<String, Object>> userDefinedTypeList;


    private SchemaInfoImpl(@Nullable String catalog, @Nullable String schema, Map<String, TableInfo> tableMap,
                           List<Map<String, Object>> userDefinedTypeList) {
        this.catalog = catalog;
        this.schema = schema;
        this.builderMap = Collections.unmodifiableMap(tableMap);
        this.userDefinedTypeList = _Collections.asUnmodifiableList(userDefinedTypeList);
    }


    @Override
    public String catalog() {
        return this.catalog;
    }

    @Override
    public String schema() {
        return this.schema;
    }

    @Override
    public Map<String, TableInfo> tableMap() {
        return this.builderMap;
    }

    @Override
    public List<Map<String, Object>> userDefinedTypeList() {
        return this.userDefinedTypeList;
    }


}
