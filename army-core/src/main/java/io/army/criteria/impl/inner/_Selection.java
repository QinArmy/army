package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.TableField;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;

public interface _Selection extends Selection {

    void appendSelection(_SqlContext context);

    @Nullable
    TableField tableField();

    Expression selectionExp();


}
