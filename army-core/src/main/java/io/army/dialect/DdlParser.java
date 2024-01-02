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

package io.army.dialect;


import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.schema._SchemaResult;

import java.util.List;

/**
 * <p>
 * This interface representing ddl parser. The result of this interface will be used by session factory for updating schema.
 *
 * @see DialectParser#schemaDdl(_SchemaResult)
 * @since 0.6.0
 */
interface DdlParser {

    /**
     * If non-empty,then the result of this interface couldn't bee used. {@link DialectParser#schemaDdl(_SchemaResult)} must throw {@link io.army.meta.MetaException}
     */
    List<String> errorMsgList();

    void dropTable(List<TableMeta<?>> tableList, List<String> sqlList);

    <T> void createTable(TableMeta<T> table, List<String> sqlList);

    void addColumn(List<FieldMeta<?>> fieldList, List<String> sqlList);

    void modifyTableComment(TableMeta<?> table, List<String> sqlList);

    void modifyColumn(List<_FieldResult> resultList, List<String> sqlList);

    <T> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);

    <T> void changeIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);

    <T> void dropIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);
}
