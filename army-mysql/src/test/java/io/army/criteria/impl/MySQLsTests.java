package io.army.criteria.impl;

import io.army.domain.IDomain;
import io.army.meta.ChildDomain;
import io.army.meta.TableMeta;

import java.util.Collections;

public class MySQLsTests {


    public <P extends IDomain, T extends P> void update57(ChildDomain<P, T> domain) {
        MySQLs.singleUpdate57()
                .update(domain, "")
                .ifSet(Collections.emptyList(), Collections.emptyList())
                .where(Collections.emptyList())
                .orderBy(Collections.emptyList())
                .limit(2)
                .asUpdate();
    }

    public void multiUpdate57(TableMeta<?> table) {
        MySQLs.multiUpdate57()
                .update(table)
                .as("")
                .use().index().forJoin(Collections.emptyList())
                .join(table).as("")
                .on(Collections.emptyList())
                .join(table).as("s")
                .on(Collections.emptyList())
                .set(Collections.emptyList(), Collections.emptyList())
                .where(Collections.emptyList())
                .asUpdate();

    }


}
