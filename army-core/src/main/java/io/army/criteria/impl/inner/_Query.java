package io.army.criteria.impl.inner;

import io.army.criteria.*;
import io.army.lang.Nullable;

import java.util.List;

public interface _Query extends _PartQuery {

    List<SQLModifier> modifierList();

    List<? extends SelectPart> selectPartList();

    List<? extends _TableBlock> tableBlockList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();

    /**
     * @return a unmodifiable list
     */
    List<SortPart> groupPartList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> havingList();

    @Nullable
    LockMode lockMode();


}
