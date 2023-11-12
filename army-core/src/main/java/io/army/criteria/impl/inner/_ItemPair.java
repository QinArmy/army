package io.army.criteria.impl.inner;

import io.army.criteria.ItemPair;
import io.army.criteria.SqlField;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;

import java.util.List;

public interface _ItemPair extends ItemPair {


    /**
     * @param sqlBuilder must be {@link _SqlContext#sqlBuilder()} of context . For reducing method invoking.
     */
    void appendItemPair(StringBuilder sqlBuilder, _SetClauseContext context);


    interface _FieldItemPair extends _ItemPair {

        SqlField field();

        _Expression value();

    }

    interface _RowItemPair extends _ItemPair {

        List<? extends SqlField> rowFieldList();

    }


}
