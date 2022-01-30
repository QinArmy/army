package io.army.dialect.mysql;


import io.army.dialect.Dialect;
import io.army.dialect._AbstractDialect;
import io.army.dialect._MockDialects;
import io.army.example.domain.User_;
import io.army.meta.MetaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class MySQLDdlTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLDdlTests.class);

    @Test
    public void createTable() {
        final List<String> sqlList = new ArrayList<>();
        MySQLDdl ddl;
        for (Dialect dialect : Dialect.values()) {

            ddl = new MySQLDdl((_AbstractDialect) _MockDialects.from(dialect));
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

}
