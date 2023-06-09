package io.army.dialect.postgre;

import io.army.dialect._MockDialects;
import io.army.mapping.PostgreFullType_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class PostgreDdlTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreDdlTests.class);

    @Test
    public void createTable() {
        final PostgreParser parser;
        parser = (PostgreParser) _MockDialects.from(PostgreDialect.POSTGRE15);

        final PostgreDdlParser ddlParser;
        ddlParser = PostgreDdlParser.create(parser);

        final List<String> sqlList = new ArrayList<>();
        ddlParser.createTable(PostgreFullType_.T, sqlList);
        for (String sql : sqlList) {
            System.out.print(sql);
            System.out.print(';');
            System.out.println();
        }

    }


}
