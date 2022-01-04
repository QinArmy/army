package io.army.criteria.impl;

import io.army.criteria.impl.inner._MultiUpdate;
import io.army.lang.Nullable;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 */
abstract class MultiUpdate<C, JT, JS, WR, WA, SR> extends AbstractUpdate<C, JT, JS, WR, WA, SR>
        implements _MultiUpdate {

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;

    MultiUpdate(@Nullable C criteria) {
        super(criteria);
    }


    @Override
    final JT getNoActionTableBlock() {
        JT noActionTableBlock = this.noActionTableBlock;
        if (noActionTableBlock == null) {
            noActionTableBlock = createNoActionTableBlock();
            this.noActionTableBlock = noActionTableBlock;
        }
        return noActionTableBlock;
    }

    @Override
    final JS getNoActionTablePartBlock() {
        JS block = this.noActionTablePartBlock;
        if (block == null) {
            block = createNoActionTablePartBlock();
            this.noActionTablePartBlock = block;
        }
        return block;
    }


}
