package io.army.criteria.impl.inner;

import io.army.criteria.SelectItem;
import io.army.dialect._SqlContext;

public interface _SelectItem extends SelectItem {

    /**
     * @param sqlBuilder must be {@link _SqlContext#sqlBuilder()} of context . For reducing method invoking.
     */
    void appendSelectItem(StringBuilder sqlBuilder, _SqlContext context);

}
