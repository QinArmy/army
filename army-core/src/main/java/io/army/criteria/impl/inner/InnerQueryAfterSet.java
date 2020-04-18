package io.army.criteria.impl.inner;

import io.army.criteria.SQLAble;
import io.army.criteria.SortPart;

import java.util.List;

@DeveloperForbid
public interface InnerQueryAfterSet extends SQLAble {

    List<SortPart> orderPartList();

    int offset();

    int rowCount();

}
