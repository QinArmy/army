package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Delete;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p>
 * This class is base class of multi-table delete implementation.
 * </p>
 */
abstract class MultiDelete<C, JT, JS, WR, WA> extends AbstractDml<C, JT, JS, WR, WA>
        implements Delete, Delete.DeleteSpec, _MultiDelete {

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;

    private boolean prepared;

    List<_TableBlock> tableBlockList = new ArrayList<>();


    MultiDelete(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final Delete asDelete() {
        _Assert.nonPrepared(this.prepared);
        //TODO clear context

        this.noActionTableBlock = null;
        this.noActionTablePartBlock = null;

        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (CollectionUtils.isEmpty(tableBlockList)) {
            throw new CriteriaException("Not found any table for multi-table delete.");
        }
        this.tableBlockList = Collections.unmodifiableList(tableBlockList);


        final List<_Predicate> predicateList = this.predicateList;
        if (CollectionUtils.isEmpty(predicateList)) {
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
