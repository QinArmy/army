package io.army.session;

import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.sqltype.SqlType;

import java.util.List;

public interface ResultRecordMeta extends ResultItem, ResultItem.ResultAccessSpec {

    List<? extends Selection> selectionList();

    List<String> columnLabelList();

    Selection getSelection(int indexBasedZero);

    SqlType getSqlType(int indexBasedZero);

    @Nullable
    <T> T getOf(int indexBasedZero, Option<T> option);

    <T> T getNonNullOf(int indexBasedZero, Option<T> option);


}
