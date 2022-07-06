package io.army.criteria.impl.inner;

import io.army.criteria.Selection;
import io.army.criteria.TableField;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;

public interface _Selection extends Selection, _SelfDescribed {

    void appendSelection(_SqlContext context);

    @Nullable
    TableField tableField();


    /**
     * <p>
     * don't output AS clause
     * </p>
     */
    @Override
    void appendSql(_SqlContext context);


}
