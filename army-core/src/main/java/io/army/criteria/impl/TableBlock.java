package io.army.criteria.impl;

import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class TableBlock implements _TableBlock {

    final TablePart tablePart;

    final _JoinType joinType;

    @Deprecated
    TableBlock(TablePart tablePart, _JoinType joinType) {
        this.tablePart = tablePart;
        this.joinType = joinType;
    }

    TableBlock(_JoinType joinType, TablePart tablePart) {
        this.joinType = joinType;
        this.tablePart = tablePart;

    }

    @Override
    public final TablePart table() {
        return this.tablePart;
    }

    @Override
    public final _JoinType jointType() {
        return this.joinType;
    }


    static TableBlock firstBlock(TablePart tablePart, String alias) {
        Objects.requireNonNull(tablePart);
        return new SimpleTableBlock(tablePart, alias);
    }


    static class SimpleTableBlock extends TableBlock {

        private final String alias;

        SimpleTableBlock(TablePart tablePart, String alias) {
            super(tablePart, _JoinType.NONE);
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
