package io.army.criteria.impl;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import org.springframework.lang.NonNull;

/**
 * created  on 2018-12-24.
 */
class JoinImpl<X extends IDomain> extends AbstractJoin<X> {


    final TableMeta<X> table;

    JoinImpl(@NonNull InnerSelectList selectList, @NonNull TableMeta<X> table) {
        super(selectList);
        Assert.assertNotNull(table, "tableElement required");
        this.table = table;
    }

    @Override
    protected String alias() {
        return "";
    }
}
