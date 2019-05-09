package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.LimitAble;
import org.qinarmy.army.criteria.QueryAble;

import static org.qinarmy.army.util.Assert.assertGeZero;

/**
 * created  on 2019-01-31.
 */
abstract class AbstractLimitAble extends AbstractQueryAble implements LimitAble {

    private int offset = 0;

    private int rowCount = -1;


    @Override
    public final QueryAble limit(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final QueryAble limit(int offset, int rowCount) {
        assertGeZero(offset, "offset must great than 0 .");
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public String toString() {
        return "";
    }
}
