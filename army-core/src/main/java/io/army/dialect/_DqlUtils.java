package io.army.dialect;

import io.army.criteria.SelectItem;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

abstract class _DqlUtils extends _DialectUtils {

    static List<Selection> flatSelectParts(final List<? extends SelectItem> selectPartList) {
        final List<Selection> selectionList = new ArrayList<>(selectPartList.size());
        for (SelectItem selectItem : selectPartList) {
            if (selectItem instanceof Selection) {
                selectionList.add((Selection) selectItem);
            } else if (selectItem instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectItem).selectionList());
            } else {
                throw _Exceptions.unknownSelectPart(selectItem);
            }
        }
        return selectionList;
    }


}
