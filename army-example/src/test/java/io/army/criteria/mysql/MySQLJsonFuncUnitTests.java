package io.army.criteria.mysql;

import io.army.criteria.TabularItem;
import io.army.criteria.impl.MySQLs;
import io.army.sqltype.MySQLTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class MySQLJsonFuncUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLJsonFuncUnitTests.class);

    @Test
    public void jsonTable() {
        TabularItem item;
        item = MySQLs.jsonTable(null, null)
                .columns()
                .leftParen("")
                .forOrdinality()
                .comma("")
                .forOrdinality()
                .comma("", MySQLTypes.JSON)
                .path("")
                .defaultValue("")
                .onEmpty()
                .nullWord()
                .onError()
                .commaNested("")
                .columns()
                .leftParen("")
                .forOrdinality()
                .commaNestedPath("")
                .columns()
                .leftParen("")
                .forOrdinality()
                .rightParen()
                .rightParen()
                .rightParen();

    }


}
