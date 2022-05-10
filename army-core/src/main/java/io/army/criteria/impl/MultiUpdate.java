package io.army.criteria.impl;

import io.army.criteria.impl.inner._MultiUpdate;
import io.army.criteria.impl.inner._TableBlock;

import java.util.List;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 *
 * @since 1.0
 */
abstract class MultiUpdate<C, SR, FT, FS, FP, JT, JS, JP, WR, WA>
        extends JoinableUpdate<C, SR, FT, FS, FP, JT, JS, JP, WR, WA>
        implements _MultiUpdate, JoinableClause.ClauseSupplier {

    final CriteriaContext criteriaContext;

    private List<_TableBlock> tableBlockList;

    MultiUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
        this.criteriaContext = criteriaContext;
    }


    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        return this.tableBlockList;
    }


    @Override
    final void onAsUpdate() {
        this.tableBlockList = this.criteriaContext.clear();
        this.doOnAsUpdate();
    }

    @Override
    void onClear() {
        this.tableBlockList = null;
    }

    void doOnAsUpdate() {

    }


}
