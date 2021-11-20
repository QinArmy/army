package io.army.criteria.impl.oracle;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner._SpecialUpdate;

import java.util.List;

@DeveloperForbid
public interface InnerOracleSingleUpdate extends _SpecialUpdate {

    /**
     * @return a unmodifiable list
     */
    List<SetTargetPart> targetList();

    /**
     * @return a unmodifiable list
     */
    List<SetValuePart> valueList();

}
