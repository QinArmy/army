package io.army.criteria.impl.inner;

import io.army.criteria.ItemPair;
import io.army.criteria.SQLField;
import io.army.dialect._SetClauseContext;

import java.util.List;

public interface _ItemPair extends ItemPair {



     void appendItemPair(StringBuilder sqlBuilder, _SetClauseContext context);


    interface _FieldItemPair extends _ItemPair {

        SQLField field();

        _Expression value();

    }

    interface _RowItemPair extends _ItemPair {

        List<? extends SQLField> rowFieldList();

    }


}
