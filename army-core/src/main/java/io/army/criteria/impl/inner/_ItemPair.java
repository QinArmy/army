package io.army.criteria.impl.inner;

import io.army.criteria.DataField;
import io.army.criteria.ItemPair;
import io.army.dialect._SetClauseContext;

import java.util.List;

public interface _ItemPair extends ItemPair {


    void appendItemPair(_SetClauseContext context);


    interface _FieldItemPair {

        DataField field();

        _Expression value();

    }

    interface _RowItemPair {

        List<? extends DataField> rowFieldList();

    }


}
