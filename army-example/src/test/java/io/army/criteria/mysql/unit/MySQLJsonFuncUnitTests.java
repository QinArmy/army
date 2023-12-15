package io.army.criteria.mysql.unit;

import io.army.criteria.impl.MySQLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class MySQLJsonFuncUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLJsonFuncUnitTests.class);

    @Test
    public void jsonTable() {
        MySQLs.jsonTable();

    }


}
