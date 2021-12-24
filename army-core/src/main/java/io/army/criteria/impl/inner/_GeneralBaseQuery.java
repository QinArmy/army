package io.army.criteria.impl.inner;

import io.army.criteria.SQLModifier;

import java.util.List;

public interface _GeneralBaseQuery extends _GeneralQuery {

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> modifierList();

    /**
     * @return a unmodifiable list
     */
    List<? extends TableBlock> tableWrapperList();
}
