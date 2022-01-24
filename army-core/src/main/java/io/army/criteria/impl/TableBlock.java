package io.army.criteria.impl;

import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class TableBlock implements _TableBlock {

    final TableItem tableItem;

    final _JoinType joinType;

    @Deprecated
    TableBlock(TableItem tableItem, _JoinType joinType) {
        this.tableItem = tableItem;
        this.joinType = joinType;
    }

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


    static TableBlock firstBlock(TableItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new SimpleTableBlock(tableItem, alias);
    }


    static class SimpleTableBlock extends TableBlock {

        private final String alias;

        SimpleTableBlock(TableItem tableItem, String alias) {
            super(tableItem, _JoinType.NONE);
            this.alias = alias;
        }

        @Override
        public final List<_Predicate> predicates() {
            return Collections.emptyList();
        }

        @Override
        public final String alias() {
            return this.alias;
        }


    } // SimpleFromTableBlock


}
