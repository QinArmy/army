package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.SelectPart;
import io.army.criteria.SortPart;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

@DeveloperForbid
public interface InnerQuery extends InnerGeneralQuery {

    /**
     * @return a unmodifiable list
     */
    List<TableWrapper> tableWrapperList();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();

    /**
     * @return a unmodifiable map
     */
    Map<TableMeta<?>, Integer> tablePresentCountMap();

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> modifierList();

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
