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

import io.army.dialect.*;

public class SQLiteParserFactory extends DialectParserFactory {

    private SQLiteParserFactory() {
    }


    public static DialectParser dialectParser(final DialectEnv environment) {
        final SQLiteDialect dialect;
        dialect = (SQLiteDialect) targetDialect(environment, Database.SQLite);
        return SQLiteDialectParser.create(environment, dialect);
    }


}
