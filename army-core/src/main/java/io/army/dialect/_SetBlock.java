package io.army.dialect;


import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;

import java.util.List;

public interface _SetBlock extends _Block {

    boolean hasSelfJoint();

    List<? extends SetLeftItem> leftItemList();

    List<? extends SetRightItem> valueParts();

}
