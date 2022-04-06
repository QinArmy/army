package io.army.dialect;

import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;
import io.army.criteria.TableField;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;

import java.util.List;

public interface _SetClause {

    _SqlContext context();

    List<? extends SetLeftItem> leftItemList();

    List<? extends SetRightItem> rightItemList();

    @Nullable
    String validateField(TableField<?> field);

    /**
     * @return table alias(not safe table alias)
     */
    String tableAlias(TableField<?> field);

    /**
     * @return the table that tableAlias(not safe table alias) representing.
     */
    SingleTableMeta<?> tableOf(String tableAlias);


    boolean supportAlias();

    boolean supportRow();

}
