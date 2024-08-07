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


import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.PillUser_;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.TableMeta;
import io.army.schema.FieldResult;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MySQLDdlTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLDdlTests.class);

    private static final List<TableMeta<?>> TABLE_LIST = List.of(
            ChinaRegion_.T,
            ChinaCity_.T,
            ChinaProvince_.T,
            BankUser_.T,
            PillUser_.T,
            BankAccount_.T,
            Captcha_.T,
            BankPerson_.T,
            RegisterRecord_.T
    );

    private static final MySQLDdlParser DDL_PARSER = new MySQLDdlParser((MySQLParser) _MockDialects.from(MySQLDialect.MySQL80));

    @Test
    public void createTable() {
        final List<String> sqlList = _Collections.arrayList();

        for (TableMeta<?> table : TABLE_LIST) {
            DDL_PARSER.createTable(table, sqlList);

            List<String> errorList;
            errorList = DDL_PARSER.errorMsgList();
            if (errorList.size() > 0) {
                for (String msg : errorList) {
                    LOG.error(msg);
                }
                throw new MetaException("error");
            }
        }

        LOG.debug(_DialectUtils.printDdlSqlList(sqlList));


    }

    @SuppressWarnings("unchecked")
    @Test
    public void addColumn() {
        final List<String> sqlList = _Collections.arrayList();

        for (TableMeta<?> table : TABLE_LIST) {
            List<?> fieldList = table.fieldList();
            DDL_PARSER.addColumn((List<FieldMeta<?>>) fieldList, sqlList);
        }

        List<String> errorList;
        errorList = DDL_PARSER.errorMsgList();
        if (errorList.size() > 0) {
            for (String msg : errorList) {
                LOG.error(msg);
            }
            throw new MetaException("error");
        }

        LOG.debug(_DialectUtils.printDdlSqlList(sqlList));

    }

    @Test
    public void modifyColumn() {
        final List<String> sqlList = _Collections.arrayList();
        List<FieldResult> resultList;
        List<String> errorList;

        final Random random = ThreadLocalRandom.current();
        for (TableMeta<?> table : TABLE_LIST) {

            resultList = _Collections.arrayList();
            for (FieldMeta<?> field : table.fieldList()) {
                resultList.add(new MockFieldResult(field, (random.nextInt() & 1) == 0, (random.nextInt() & 1) == 0, (random.nextInt() & 1) == 0, (random.nextInt() & 1) == 0));
            }

            DDL_PARSER.modifyColumn(resultList, sqlList);

            errorList = DDL_PARSER.errorMsgList();
            if (errorList.size() > 0) {
                for (String msg : errorList) {
                    LOG.error(msg);
                }
                throw new MetaException("error");
            }

        }

        LOG.debug(_DialectUtils.printDdlSqlList(sqlList));


    }

    @Test
    public void createIndex() {
        final List<String> sqlList = _Collections.arrayList();
        MySQLDdlParser ddl;
        final TableMeta<?> table = PillUser_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdlParser((MySQLParser) _MockDialects.from(dialect));
            ddl.createIndex(table, Collections.singletonList("idx_identity_id"), sqlList);
            List<String> errorList;
            errorList = ddl.errorMsgList();
            if (errorList.size() > 0) {
                for (String msg : errorList) {
                    LOG.error(msg);
                }
                throw new MetaException("error");
            }

        }
        for (String sql : sqlList) {
            LOG.debug(sql);
        }

    }

    @Test
    public void changeIndex() {
        final EnumMap<MySQLDialect, List<String>> sqlMap = new EnumMap<>(MySQLDialect.class);
        MySQLDdlParser ddl;
        final TableMeta<?> table = PillUser_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdlParser((MySQLParser) _MockDialects.from(dialect));

            final List<String> sqlList = _Collections.arrayList();
            sqlMap.put(dialect, sqlList);
            ddl.changeIndex(table, Collections.singletonList("idx_identity_id"), sqlList);

            List<String> errorList;
            errorList = ddl.errorMsgList();
            if (errorList.size() > 0) {
                for (String msg : errorList) {
                    LOG.error(msg);
                }
                throw new MetaException("error");
            }

        }

        for (Map.Entry<MySQLDialect, List<String>> e : sqlMap.entrySet()) {
            LOG.debug("dialect : {}", e.getKey());
            for (String sql : e.getValue()) {
                LOG.debug(sql);
            }
        }


    }

    @Test
    public void dropIndex() {
        final EnumMap<MySQLDialect, List<String>> sqlMap = new EnumMap<>(MySQLDialect.class);
        MySQLDdlParser ddl;
        final TableMeta<?> table = PillUser_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdlParser((MySQLParser) _MockDialects.from(dialect));

            final List<String> sqlList = _Collections.arrayList();
            sqlMap.put(dialect, sqlList);
            ddl.dropIndex(table, Collections.singletonList("idx_identity_id"), sqlList);

            List<String> errorList;
            errorList = ddl.errorMsgList();
            if (errorList.size() > 0) {
                for (String msg : errorList) {
                    LOG.error(msg);
                }
                throw new MetaException("error");
            }

        }

        for (Map.Entry<MySQLDialect, List<String>> e : sqlMap.entrySet()) {
            LOG.debug("dialect : {}", e.getKey());
            for (String sql : e.getValue()) {
                LOG.debug(sql);
            }
        }


    }


    private static final class MockFieldResult implements FieldResult {

        private final FieldMeta<?> field;

        private final boolean sqlType;

        private final boolean defaultValue;

        private final boolean notNull;

        private final boolean comment;

        private MockFieldResult(FieldMeta<?> field, boolean sqlType, boolean defaultValue, boolean notNull, boolean comment) {
            this.field = field;
            this.sqlType = sqlType;
            this.defaultValue = defaultValue;
            this.notNull = notNull;
            this.comment = comment;
        }

        @Override
        public FieldMeta<?> field() {
            return this.field;
        }

        @Override
        public boolean containSqlType() {
            return this.sqlType;
        }

        @Override
        public boolean containDefault() {
            return this.defaultValue;
        }

        @Override
        public boolean containNotNull() {
            return this.notNull;
        }

        @Override
        public boolean containComment() {
            return this.comment;
        }

    }// MockFieldResult


}
