package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.SelectPart;
import io.army.criteria.SortPart;

import java.util.List;

@DeveloperForbid
public interface InnerQuery extends InnerGeneralBaseQuery {


    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();


    /**
     * @return a unmodifiable list
     */
    List<SelectPart> selectPartList();

    /**
     * @return a unmodifiable list
     */
    List<SortPart> groupPartList();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> havingList();

    /**
     * @return a unmodifiable list
     */
    List<SortPart> orderPartList();

    int offset();

    int rowCount();

}
