package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.WithElement;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 */
@Deprecated
abstract class MultiUpdate<C, JT, JS, JP, WR, WA, SR> extends JoinableUpdate<C, JT, JS, JP, WR, WA, SR>
        implements _MultiUpdate {


    private JT noActionTableBlock;

    private JS noActionOnBlock;

    private List<_TableBlock> tableBlockList = new ArrayList<>();

    MultiUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }


    @Override
    final void onAsUpdate() {
        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (_CollectionUtils.isEmpty(tableBlockList)) {
            throw new CriteriaException("multi-table table block list must not empty.");
        }
        this.tableBlockList = this.criteriaContext.clear();
        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        this.noActionTableBlock = null;
        this.noActionOnBlock = null;
        this.doOnAsUpdate();
    }

    @Override
    void onClear() {
        this.tableBlockList = null;
    }

    void doOnAsUpdate() {

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
    final JS getNoActionOnBlock() {
        JS block = this.noActionOnBlock;
        if (block == null) {
            block = createNoActionOnBlock();
            this.noActionOnBlock = block;
        }
        return block;
    }


}
