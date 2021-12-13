package io.army.criteria.impl.inner;

import java.util.List;

public interface _Query extends _GeneralBaseQuery {


    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();

    /**
     * @return a unmodifiable list
     */
    List<_SortPart> groupPartList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> havingList();

    /**
     * @return a unmodifiable list
     */
    List<_SortPart> orderPartList();

    int offset();

    int rowCount();

}
