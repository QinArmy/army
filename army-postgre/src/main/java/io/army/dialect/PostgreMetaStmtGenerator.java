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

final class PostgreMetaStmtGenerator implements MetaStmtGenerator {


    static PostgreMetaStmtGenerator create() {
        return new PostgreMetaStmtGenerator();
    }


    private PostgreMetaStmtGenerator() {
    }

    @Override
    public String queryUserDefinedTypeStmts() {
        // 1. 确定 自定义 composite 的方法 : pg_type.typcategory is 'C' 且 pg_class.relkind is 'c'
        // 2. 确定 domain 的方法 pg_type.typbasetype > 0
        return """
                SELECT t.typname AS "typeName", t.typcategory AS "type", et."enumLabelArray", at.*
                FROM pg_namespace AS n
                         JOIN pg_type AS t ON t.typnamespace = n.oid
                         LEFT JOIN LATERAL (
                    SELECT e.enumtypid, array_agg(e.enumlabel) AS "enumLabelArray"
                    FROM pg_enum AS e
                    WHERE e.enumtypid = t.oid
                    GROUP BY e.enumtypid
                    ) AS et ON et.enumtypid = t.oid
                         LEFT JOIN pg_class AS c ON c.oid = t.typrelid
                         LEFT JOIN LATERAL (
                    SELECT a.attrelid,
                           array_agg(a.attnum)   AS "columnNumArray",
                           array_agg(a.attname)  AS "columnNameArray",
                           array_agg(st.typname) AS "columnTypeArray"
                    FROM pg_attribute AS a
                             JOIN pg_type AS st ON st.oid = a.atttypid
                    WHERE a.attrelid = c.oid
                    GROUP BY a.attrelid
                    ) AS at ON at.attrelid = c.oid
                                
                WHERE n.nspname NOT IN ('pg_catalog', 'information_schema')
                  AND (t.typcategory IN ('E', 'U', 'R') OR t.typbasetype > 0 OR (t.typcategory = 'C' AND c.relkind = 'c'))
                """;
    }


}
