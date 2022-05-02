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

    TableBlock(_JoinType joinType, TableItem tableItem) {
        this.joinType = joinType;
        this.tableItem = tableItem;

    }

    @Override
    public final TableItem tableItem() {
        return this.tableItem;
    }

    @Override
    public final _JoinType jointType() {
        return this.joinType;
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

        private final String alias;

        public NoOnTableBlock(_JoinType joinType, TableItem tableItem, String alias) {
            super(joinType, tableItem);
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }
            this.alias = alias;

        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final List<_Predicate> predicates() {
            return Collections.emptyList();
        }

    }//NoOnTableBlock


}
