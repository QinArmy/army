package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Delete;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.meta.TableMeta;
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
abstract class MultiDelete<C, DR, JT, JS, WR, WA> extends AbstractDml<C, JT, JS, WR, WA>
        implements Delete, Delete.DeleteSpec, _MultiDelete {

    final CriteriaContext criteriaContext;

    private JT noActionTableBlock;

    private JS noActionTablePartBlock;

    private boolean prepared;

    private List<_TableBlock> tableBlockList = new ArrayList<>();


    MultiDelete(CriteriaContext criteriaContext) {
        super(criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
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

    @Override
    final JT addTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(joinType, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    final JS addOnBlock(_JoinType joinType, TablePart tablePart, String alias) {
        final JS block;
        block = createOnBlock(joinType, tablePart, alias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    final void addOtherBlock(_TableBlock block) {
        this.tableBlockList.add(block);
    }

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TablePart tablePart, String alias);

    abstract void onAsDelete();

    abstract void onClear();

    final DR addFirstBlock(TableBlock block) {
        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList.size() > 0) {
            throw _Exceptions.castCriteriaApi();
        }
        tableBlockList.add(block);
        return (DR) this;
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
