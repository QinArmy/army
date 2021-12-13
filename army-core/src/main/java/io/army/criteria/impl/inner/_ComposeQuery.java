package io.army.criteria.impl.inner;

import java.util.List;

public interface _ComposeQuery extends _SelfDescribed, _GeneralQuery {

    boolean requiredBrackets();

    /**
     * @return a unmodifiable list
     */
    List<_SortPart> orderByList();

    int offset();

    int rowCount();

}
