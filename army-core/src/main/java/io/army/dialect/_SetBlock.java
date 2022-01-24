package io.army.dialect;


import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValueItem;

import java.util.List;

public interface _SetBlock extends _Block {

    boolean hasSelfJoint();

    List<? extends SetTargetPart> targetParts();

    List<? extends SetValueItem> valueParts();

}
