package io.army.criteria.impl;

import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class TableBlock implements _TableBlock {

    final _JoinType joinType;

    final TableItem tableItem;

    final String alias;

    TableBlock(_JoinType joinType, TableItem tableItem, String alias) {
        Objects.requireNonNull(alias);
        this.joinType = joinType;
        this.tableItem = tableItem;
        this.alias = alias;

    }

    TableBlock(BlockParams params) {
        this.joinType = params.joinType();
        this.tableItem = params.tableItem();
        this.alias = params.alias();
    }

    @Override
    public final TableItem tableItem() {
        return this.tableItem;
    }

    @Override
    public final _JoinType jointType() {
        return this.joinType;
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    static TableBlock noneBlock(TableItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new NoOnTableBlock(_JoinType.NONE, tableItem, alias);
    }

    static TableBlock crossBlock(TableItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new NoOnTableBlock(_JoinType.CROSS_JOIN, tableItem, alias);
    }


    static class NoOnTableBlock extends TableBlock {

        NoOnTableBlock(_JoinType joinType, TableItem tableItem, String alias) {
            super(joinType, tableItem, alias);
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }

        }

        NoOnTableBlock(BlockParams params) {
            super(params);
            switch (this.joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }

        }


        @Override
        public final List<_Predicate> predicateList() {
            return Collections.emptyList();
        }

    }//NoOnTableBlock


    interface BlockParams {

        _JoinType joinType();

        TableItem tableItem();

        String alias();

    }


}
