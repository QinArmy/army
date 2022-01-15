package io.army.dialect;

import io.army.criteria.SelectPart;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

abstract class _DqlUtils extends _DialectUtils {

    static List<Selection> flatSelectParts(final List<SelectPart> selectPartList) {
        final List<Selection> selectionList = new ArrayList<>(selectPartList.size());
        for (SelectPart selectPart : selectPartList) {
            if (selectPart instanceof Selection) {
                selectionList.add((Selection) selectPart);
            } else if (selectPart instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectPart).selectionList());
            } else {
                throw _Exceptions.unknownSelectPart(selectPart);
            }
        }
        return selectionList;
    }


}
