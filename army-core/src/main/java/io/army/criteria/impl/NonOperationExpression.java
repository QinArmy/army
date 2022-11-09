package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.ItemExpression;
import io.army.criteria.SqlValueParam;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@code  SQLs.DefaultWord}</li>
 *         <li>{@code SQLs.NullWord}</li>
 *     </ul>
 * </p>
 */
abstract class NonOperationExpression<I extends Item> implements ArmyExpression, ItemExpression<I> {


    NonOperationExpression() {
    }


    @Override
    public final boolean isNullValue() {
        final boolean nullable;
        if (this instanceof SqlValueParam.SingleNonNamedValue) {
            nullable = ((SqlValueParam.SingleNonNamedValue) this).value() == null;
        } else {
            nullable = false;
        }
        return nullable;
    }





    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
