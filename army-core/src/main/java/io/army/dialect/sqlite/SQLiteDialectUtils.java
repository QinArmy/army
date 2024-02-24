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

import io.army.dialect._DialectUtils;
import io.army.util._Collections;

import java.util.Set;

abstract class SQLiteDialectUtils extends _DialectUtils {

    private SQLiteDialectUtils() {
    }

    static Set<String> createKeyWordSet() {
        final Set<String> set = _Collections.hashSet();

        set.add("ABORT");
        set.add("ACTION");
        set.add("ADD");
        set.add("AFTER");

        set.add("ALL");
        set.add("ALTER");
        set.add("ALWAYS");
        set.add("ANALYZE");

        set.add("AND");
        set.add("AS");
        set.add("ASC");
        set.add("ATTACH");

        set.add("AUTOINCREMENT");
        set.add("BEFORE");
        set.add("BEGIN");
        set.add("BETWEEN");

        set.add("BY");
        set.add("CASCADE");
        set.add("CASE");
        set.add("CAST");

        set.add("CHECK");
        set.add("COLLATE");
        set.add("COLUMN");
        set.add("COMMIT");

        set.add("CONFLICT");
        set.add("CONSTRAINT");
        set.add("CREATE");
        set.add("CROSS");

        set.add("CURRENT");
        set.add("CURRENT_DATE");
        set.add("CURRENT_TIME");
        set.add("CURRENT_TIMESTAMP");

        set.add("DATABASE");
        set.add("DEFAULT");
        set.add("DEFERRABLE");
        set.add("DEFERRED");

        set.add("DELETE");
        set.add("DESC");
        set.add("DETACH");
        set.add("DISTINCT");

        set.add("DO");
        set.add("DROP");
        set.add("EACH");
        set.add("ELSE");

        set.add("END");
        set.add("ESCAPE");
        set.add("EXCEPT");
        set.add("EXCLUDE");

        set.add("EXCLUSIVE");
        set.add("EXISTS");
        set.add("EXPLAIN");
        set.add("FAIL");

        set.add("FILTER");
        set.add("FIRST");
        set.add("FOLLOWING");
        set.add("FOR");

        set.add("FOREIGN");
        set.add("FROM");
        set.add("FULL");
        set.add("GENERATED");

        set.add("GLOB");
        set.add("GROUP");
        set.add("GROUPS");
        set.add("HAVING");

        set.add("IF");
        set.add("IGNORE");
        set.add("IMMEDIATE");
        set.add("IN");

        set.add("INDEX");
        set.add("INDEXED");
        set.add("INITIALLY");
        set.add("INNER");

        set.add("INSERT");
        set.add("INSTEAD");
        set.add("INTERSECT");
        set.add("INTO");

        set.add("IS");
        set.add("ISNULL");
        set.add("JOIN");
        set.add("KEY");

        set.add("LAST");
        set.add("LEFT");
        set.add("LIKE");
        set.add("LIMIT");

        set.add("MATCH");
        set.add("MATERIALIZED");
        set.add("NATURAL");
        set.add("NO");

        set.add("NOT");
        set.add("NOTHING");
        set.add("NOTNULL");
        set.add("NULL");

        set.add("NULLS");
        set.add("OF");
        set.add("OFFSET");
        set.add("ON");

        set.add("OR");
        set.add("ORDER");
        set.add("OTHERS");
        set.add("OUTER");

        set.add("OVER");
        set.add("PARTITION");
        set.add("PLAN");
        set.add("PRAGMA");

        set.add("PRECEDING");
        set.add("PRIMARY");
        set.add("QUERY");
        set.add("RAISE");

        set.add("RANGE");
        set.add("RECURSIVE");
        set.add("REFERENCES");
        set.add("REGEXP");

        set.add("REINDEX");
        set.add("RELEASE");
        set.add("RENAME");
        set.add("REPLACE");

        set.add("RESTRICT");
        set.add("RETURNING");
        set.add("RIGHT");
        set.add("ROLLBACK");

        set.add("ROW");
        set.add("ROWS");
        set.add("SAVEPOINT");
        set.add("SELECT");

        set.add("SET");
        set.add("TABLE");
        set.add("TEMP");
        set.add("TEMPORARY");

        set.add("THEN");
        set.add("TIES");
        set.add("TO");
        set.add("TRANSACTION");

        set.add("TRIGGER");
        set.add("UNBOUNDED");
        set.add("UNION");
        set.add("UNIQUE");

        set.add("UPDATE");
        set.add("USING");
        set.add("VACUUM");
        set.add("VALUES");

        set.add("VIEW");
        set.add("VIRTUAL");
        set.add("WHEN");
        set.add("WHERE");

        set.add("WINDOW");
        set.add("WITH");
        set.add("WITHOUT");

        return set;
    }
}
