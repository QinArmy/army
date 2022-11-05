package io.army.dialect.mysql;


import io.army.dialect.ArmyParser;
import io.army.dialect.Database;
import io.army.dialect._MockDialects;
import io.army.example.pill.domain.User_;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;

public class MySQLDdlTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLDdlTests.class);

    @Test
    public void createTable() {
        final List<String> sqlList = new ArrayList<>();
        MySQLDdl ddl;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdl((ArmyParser) _MockDialects.from(dialect));
            ddl.createTable(User_.T, sqlList);

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

    @SuppressWarnings("unchecked")
    @Test
    public void addColumn() {
        final List<String> sqlList = new ArrayList<>();
        MySQLDdl ddl;
        final TableMeta<?> table = User_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdl((ArmyParser) _MockDialects.from(dialect));
            List<?> fieldList = table.fieldList();
            ddl.addColumn((List<FieldMeta<?>>) fieldList, sqlList);
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
    public void modifyColumn() {
        final List<String> sqlList = new ArrayList<>();
        MySQLDdl ddl;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdl((ArmyParser) _MockDialects.from(dialect));

            List<_FieldResult> resultList = new ArrayList<>();
            resultList.add(new MockFieldResult(User_.nickName, false, true, false, false));

            resultList.add(new MockFieldResult(User_.identityId, true, true, false, true));
            resultList.add(new MockFieldResult(User_.identityType, false, true, false, false));

            ddl.modifyColumn(resultList, sqlList);

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
    public void createIndex() {
        final List<String> sqlList = new ArrayList<>();
        MySQLDdl ddl;
        final TableMeta<?> table = User_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdl((ArmyParser) _MockDialects.from(dialect));
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
        MySQLDdl ddl;
        final TableMeta<?> table = User_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdl((ArmyParser) _MockDialects.from(dialect));

            final List<String> sqlList = new ArrayList<>();
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
        MySQLDdl ddl;
        final TableMeta<?> table = User_.T;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.database() != Database.MySQL) {
                continue;
            }
            ddl = new MySQLDdl((ArmyParser) _MockDialects.from(dialect));

            final List<String> sqlList = new ArrayList<>();
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


    private static final class MockFieldResult implements _FieldResult {

        private final FieldMeta<?> field;

        private final boolean sqlType;

        private final boolean defaultValue;

        private final boolean nullable;

        private final boolean comment;

        private MockFieldResult(FieldMeta<?> field, boolean sqlType, boolean defaultValue, boolean nullable, boolean comment) {
            this.field = field;
            this.sqlType = sqlType;
            this.defaultValue = defaultValue;
            this.nullable = nullable;
            this.comment = comment;
        }

        @Override
        public FieldMeta<?> field() {
            return this.field;
        }

        @Override
        public boolean sqlType() {
            return this.sqlType;
        }

        @Override
        public boolean defaultValue() {
            return this.defaultValue;
        }

        @Override
        public boolean nullable() {
            return this.nullable;
        }

        @Override
        public boolean comment() {
            return this.comment;
        }

    }// MockFieldResult


}
