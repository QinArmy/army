package io.army.dialect.postgre;

import io.army.dialect._Constant;
import io.army.dialect._MockDialects;
import io.army.mapping.PostgreFullType_;
import io.army.meta.MetaException;
import io.army.modelgen._MetaBridge;
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

        if (ddlParser.errorMsgList().size() > 0) {
            throw new MetaException(_MetaBridge.createErrorMessage("meta error", ddlParser.errorMsgList()));
        }
        final StringBuilder builder = new StringBuilder(128);

        for (String sql : sqlList) {
            builder.append(sql)
                    .append(_Constant.SPACE_SEMICOLON)
                    .append('\n');
        }

        LOG.debug(builder.toString());

    }


}
