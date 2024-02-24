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

package io.army.dialect.sqlite;

import io.army.dialect._DdlParser;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.DataType;

import java.util.List;


final class SQLiteDdlParser extends _DdlParser<SQLiteParser> {

    static SQLiteDdlParser create(SQLiteParser parser) {
        return new SQLiteDdlParser(parser);
    }

    private SQLiteDdlParser(SQLiteParser parser) {
        super(parser);
    }


    @Override
    public void modifyTableComment(TableMeta<?> table, List<String> sqlList) {

    }

    @Override
    protected void dataType(FieldMeta<?> field, DataType dataType, StringBuilder builder) {

    }

    @Override
    protected void postDataType(FieldMeta<?> field, DataType dataType, StringBuilder builder) {

    }

    @Override
    protected void appendTableOption(TableMeta<?> table, StringBuilder builder) {

    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {

    }

    @Override
    protected void doModifyColumn(_FieldResult result, StringBuilder builder) {

    }
}
