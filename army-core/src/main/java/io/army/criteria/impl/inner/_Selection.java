package io.army.criteria.impl.inner;

import io.army.annotation.UpdateMode;
import io.army.criteria.Selection;
import io.army.dialect._SqlContext;

public interface _Selection extends Selection {

    void appendSelection(_SqlContext context);

    UpdateMode updateMode();
}
