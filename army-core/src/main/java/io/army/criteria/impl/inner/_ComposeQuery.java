package io.army.criteria.impl.inner;

import io.army.criteria.SelfDescribed;
import io.army.criteria.SortPart;

import java.util.List;

public interface _ComposeQuery extends SelfDescribed, _GeneralQuery {

    boolean requiredBrackets();

    /**
     * @return a unmodifiable list
     */
    List<SortPart> orderByList();

    int offset();

    int rowCount();

}
