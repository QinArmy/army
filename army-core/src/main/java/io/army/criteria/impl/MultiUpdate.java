package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 */
abstract class MultiUpdate<C, JT, JS, WR, WA, SR> extends AbstractUpdate<C, JT, JS, WR, WA, SR>
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

    final void addFirstBlock(TableBlock block) {
        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList == null || tableBlockList.size() > 0) {
            throw _Exceptions.castCriteriaApi();
        }
        tableBlockList.add(block);
        this.criteriaContext.onAddTablePart(block.tablePart, block.alias());
    }

    final _TableBlock getFirstBlock() {
        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList.size() != 1) {
            throw _Exceptions.castCriteriaApi();
        }
        return tableBlockList.get(0);
    }

    @Override
    final void onAsUpdate() {
        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (CollectionUtils.isEmpty(tableBlockList)) {
            throw new CriteriaException("multi-table table block list must not empty.");
        }
        this.tableBlockList = Collections.unmodifiableList(tableBlockList);
        this.doOnAsUpdate();
    }

    @Override
    void onClear() {
        this.tableBlockList = null;
    }

    void doOnAsUpdate() {

    }


    abstract JT createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(JoinType joinType, TablePart tablePart, String alias);


    final void addOtherBlock(_TableBlock block) {
        this.tableBlockList.add(block);
    }

    final int blockSize() {
        return this.tableBlockList.size();
    }

    @Override
    final JT addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(joinType, table, tableAlias);
        this.tableBlockList.add((_TableBlock) block);
        return block;
    }

    @Override
    final JS addOnBlock(JoinType joinType, TablePart tablePart, String alias) {
        final JS block;
        block = createOnBlock(joinType, tablePart, alias);
        this.tableBlockList.add((_TableBlock) block);
        if (!(tablePart instanceof TableMeta)) {
            this.criteriaContext.onAddTablePart(tablePart, alias);
        }
        return block;
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
