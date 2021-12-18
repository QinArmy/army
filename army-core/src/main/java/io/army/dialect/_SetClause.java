package io.army.dialect;


import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;

import java.util.List;

public interface _SetClause extends _Clause {

    boolean hasSelfJoint();

    List<? extends SetTargetPart> targetParts();

    List<? extends SetValuePart> valueParts();


}
