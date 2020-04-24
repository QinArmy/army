package io.army.criteria.impl.inner;

import io.army.criteria.Insert;
import io.army.domain.IDomain;

import java.util.List;


@DeveloperForbid
public interface InnerStandardInsert extends InnerInsert {

    /**
     * @see Insert.InsertValuesAble
     */
    List<IDomain> valueList();
}
