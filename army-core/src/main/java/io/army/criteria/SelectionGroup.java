package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface SelectionGroup extends SelectPart {

    String tableAlias();

    List<Selection> selectionList();


    interface SubQuerySelectGroup extends SelectionGroup, SemiFinished {

        void finish(SubQuery subQuery);
    }

    interface ListSelectGroup extends SelectionGroup, SemiFinished {

        /**
         * @return if true then finished.
         */
        boolean tryFinish(Map<TableMeta<?>, String> tableAliasMap);

    }
}
