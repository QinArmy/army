package io.army.criteria.impl.inner;

import io.army.criteria.SelfDescribed;
import io.army.criteria.SortPart;

import java.util.List;

@DeveloperForbid
public interface InnerComposeQuery extends SelfDescribed, InnerGeneralQuery {

    boolean requiredBrackets();

    /**
     * @return a unmodifiable list
     */
    List<SortPart> orderPartList();

    int offset();

    int rowCount();

}
