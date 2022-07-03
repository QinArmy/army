package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.DmlStatement;
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
abstract class MultiUpdate<C, F extends DataField, SR, FT, FS, FP, JT, JS, JP, WR, WA, U extends DmlStatement.DmlUpdate>
        extends JoinableUpdate<C, F, SR, FT, FS, FP, JT, JS, JP, WR, WA, U>
        implements _MultiUpdate, JoinableClause.ClauseSupplier {

    final CriteriaContext criteriaContext;

    private List<_TableBlock> tableBlockList;

    MultiUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
        this.criteriaContext = criteriaContext;
    }


    @Override
    public final List<_TableBlock> tableBlockList() {
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
