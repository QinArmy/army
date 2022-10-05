package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.DmlStatement;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 *
 * @since 1.0
 */
abstract class MultiUpdate<C, F extends DataField, SR, FT, FS, FP, FJ, JT, JS, JP, WR, WA, U extends DmlStatement.DmlUpdate>
        extends JoinableUpdate<C, F, SR, FT, FS, FP, FJ, JT, JS, JP, WR, WA, U>
        implements _MultiUpdate, JoinableClause.ClauseCreator<FP, JT, JS, JP> {

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
        this.tableBlockList = this.criteriaContext.endContext();
        this.doOnAsUpdate();
    }

    @Override
    void onClear() {
        this.tableBlockList = null;
    }

    void doOnAsUpdate() {

    }

    @Override
    public FP createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        throw ContextStack.castCriteriaApi(this.criteriaContext);
    }

    @Override
    public JP createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        throw ContextStack.castCriteriaApi(this.criteriaContext);
    }


}
