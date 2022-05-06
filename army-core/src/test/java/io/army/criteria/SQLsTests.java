package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.SingleTableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Collections;

public class SQLsTests {

    private static final Logger LOG = LoggerFactory.getLogger(SQLsTests.class);

    @Test
    public void standardSelect(SingleTableMeta<?> table) {
        final Select select;
        select = SQLs.query()
                .select(SQLs.group(table, "t"))
                .from(table, "t")
                .where(Collections::emptyList)
                .groupBy(Collections::emptyList)
                .having(Collections::emptyList)
                .orderBy(Collections::emptyList)
                .limit(1)
                .lock(LockMode.READ)
                .union(this::createSelect)
                .limit(1)
                .asQuery();

        System.out.println(select);
    }


    private Select createSelect() {
        return null;
    }


}
