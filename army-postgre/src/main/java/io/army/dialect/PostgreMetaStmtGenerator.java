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
        return "FROM pg_namespace AS n\n" +
                "         JOIN pg_type AS t ON t.typnamespace = n.oid\n" +
                "         LEFT JOIN LATERAL (\n" +
                "    SELECT e.enumtypid, array_agg(e.enumlabel) AS \"enumLabelArray\"\n" +
                "    FROM pg_enum AS e\n" +
                "    WHERE e.enumtypid = t.oid\n" +
                "    GROUP BY e.enumtypid\n" +
                "    ) AS et ON et.enumtypid = t.oid\n" +
                "         LEFT JOIN pg_class AS c ON c.oid = t.typrelid\n" +
                "         LEFT JOIN LATERAL (\n" +
                "    SELECT a.attrelid,\n" +
                "           array_agg(a.attnum)   AS \"columnNumArray\",\n" +
                "           array_agg(a.attname)  AS \"columnNameArray\",\n" +
                "           array_agg(st.typname) AS \"columnTypeArray\"\n" +
                "    FROM pg_attribute AS a\n" +
                "             JOIN pg_type AS st ON st.oid = a.atttypid\n" +
                "    WHERE a.attrelid = c.oid\n" +
                "    GROUP BY a.attrelid\n" +
                "    ) AS at ON at.attrelid = c.oid\n" +
                "\n" +
                "WHERE n.nspname NOT IN ('pg_catalog', 'information_schema')\n" +
                "  AND (t.typcategory IN ('E', 'U', 'C', 'R') OR t.typbasetype > 0)\n" +
                "  AND (c.relkind IS NULL OR c.relkind = 'c')";
    }


}
