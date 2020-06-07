package io.army.criteria.impl.inner;

import io.army.criteria.SQLModifier;

import java.util.List;

@DeveloperForbid
public interface InnerGeneralBaseQuery extends InnerGeneralQuery {

    /**
     * @return a unmodifiable list
     */
    List<SQLModifier> modifierList();

    /**
     * @return a unmodifiable list
     */
    List<? extends TableWrapper> tableWrapperList();
}
