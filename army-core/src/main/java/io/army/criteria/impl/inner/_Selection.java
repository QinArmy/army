package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.TableField;
import io.army.lang.Nullable;

public interface _Selection extends Selection, _SelectItem {


    @Nullable
    TableField tableField();

    /**
     * @return <ul>
     * <li>If this is {@link io.army.criteria.DerivedField}, then return {@link Selection} underlying expression</li>
     * <li>Else return this</li>
     * </ul>
     */
    @Nullable
    Expression underlyingExp();


}
