package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.SelectPart;
import io.army.criteria.Selection;
import io.army.criteria.SelectionGroup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class CriteriaUtils {

    CriteriaUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * invoke after {@code asSelect()}
     *
     * @return a unmodifiable map
     */
    static Map<String, Selection> createSelectionMap(List<SelectPart> selectPartList) {

        Map<String, Selection> selectionMap = new HashMap<>();
        for (SelectPart selectPart : selectPartList) {

            if (selectPart instanceof Selection) {
                Selection selection = (Selection) selectPart;
                if (selectionMap.putIfAbsent(selection.alias(), selection) != selection) {
                    throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION, "selection[%s] duplication"
                            , selection);
                }
            } else if (selectPart instanceof SelectionGroup) {
                SelectionGroup group = (SelectionGroup) selectPart;
                String tableAlias = group.tableAlias();
                for (Selection selection : group.selectionList()) {
                    if (selectionMap.putIfAbsent(tableAlias, selection) != selection) {
                        throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION, "selection[%s] duplication"
                                , selection);
                    }
                }
            }

        }
        return Collections.unmodifiableMap(selectionMap);
    }

}
