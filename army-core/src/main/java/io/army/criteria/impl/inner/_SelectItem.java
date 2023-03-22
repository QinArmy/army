package io.army.criteria.impl.inner;

import io.army.criteria.SelectItem;
import io.army.dialect._SqlContext;

public interface _SelectItem extends SelectItem {

    void appendSelectItem(_SqlContext context);

}
