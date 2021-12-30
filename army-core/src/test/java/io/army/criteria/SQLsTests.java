package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.SingleTableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class SQLsTests {

    private static final Logger LOG = LoggerFactory.getLogger(SQLsTests.class);

    @Test
    public void standardSelect(SingleTableMeta<?> table) {
        final Select select;
        select = SQLs.standardSelect()
                .select(SQLs.group(table, "t"))
                .union()
                .select(SQLs.group(table, "t"))
                .from(table, "t")
                .asQuery();

        System.out.println(select);
    }


}
