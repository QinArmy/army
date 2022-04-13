package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.WithElement;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;


/**
 * <p>
 * This class is base class of multi-table delete implementation.
 * </p>
 */
abstract class MultiDelete<C, JT, JS, JP, WR, WA> extends JoinableDml<C, JT, JS, JP, WR, WA>
        implements Delete, Delete.DeleteSpec, _MultiDelete {


    private JT noActionTableBlock;

    private JS noActionTablePartBlock;

    private boolean prepared;

    private List<_TableBlock> tableBlockList;


    MultiDelete(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final Delete asDelete() {
        _Assert.nonPrepared(this.prepared);
        this.validateBeforeClearContext();
        this.tableBlockList = this.criteriaContext.clear();
        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }

        this.noActionTableBlock = null;
        this.noActionTablePartBlock = null;
        final List<_Predicate> predicateList = this.predicateList;
        if (_CollectionUtils.isEmpty(predicateList)) {
            throw _Exceptions.dmlNoWhereClause();
        }
        this.predicateList = Collections.unmodifiableList(predicateList);

        this.onAsDelete();

        this.prepared = true;
        return this;
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;
        this.predicateList = null;
        this.tableBlockList = null;
        this.onClear();
    }

    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        _Assert.prepared(this.prepared);
        return this.tableBlockList;
    }


    abstract void onAsDelete();

    abstract void onClear();

    void validateBeforeClearContext() {
        // no-op
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
        JS block = this.noActionTablePartBlock;
        if (block == null) {
            block = createNoActionOnBlock();
            this.noActionTablePartBlock = block;
        }
        return block;
    }


}
