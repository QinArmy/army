package io.army.session;

import io.army.criteria.Selection;
import io.army.sqltype.SQLType;

import javax.annotation.Nullable;
import java.util.List;

public interface ResultRecordMeta extends ResultItem, ResultItem.ResultAccessSpec {

    List<? extends Selection> selectionList();

    List<String> columnLabelList();

    Selection getSelection(int indexBasedZero);

    SQLType getSqlType(int indexBasedZero);

    ArmyType getArmyType(int indexBasedZero);

    @Nullable
    <T> T getOf(int indexBasedZero, Option<T> option);

    <T> T getNonNullOf(int indexBasedZero, Option<T> option);


}
