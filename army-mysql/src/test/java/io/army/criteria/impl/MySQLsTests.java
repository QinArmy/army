package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.domain.IDomain;
import io.army.meta.ChildDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collections;

public class MySQLsTests {


    public <P extends IDomain, T extends P> void update57(ChildDomain<P, T> domain) {
        FieldMeta<P, ?> field = null;
        MySQLs57.singleUpdate57()
                .update(domain, "")
                .set(field, 0)
                .where(Collections.emptyList())
                .orderBy(Collections.emptyList())
                .limit(2)
                .asUpdate();
    }

    public void multiUpdate57(TableMeta<?> table) {
        MySQLs57.multiUpdate57()
                .update(table).partition("").as("")
                .useIndex(Collections.emptyList())
                .join(table, "")
                .on(Collections.emptyList())
                .set(Collections.emptyList(), Collections.emptyList())
                .where(Collections.emptyList())
        ;

    }


    public <T extends IDomain> void standardUpdate(TableMeta<T> table) {
        FieldMeta<T, ?> field = null;
        SQLs.standardUpdate()
                .update(table, "t")
                .set(field, 0)
                .set(field, 3)
                //.where(Collections.emptyList())
                .where((IPredicate) null)
                .and((IPredicate) null)
                .asUpdate();
    }


}
