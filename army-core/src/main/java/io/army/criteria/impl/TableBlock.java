package io.army.criteria.impl;

import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class TableBlock implements _TableBlock {

    final TablePart tablePart;

    final JoinType joinType;

    @Deprecated
    TableBlock(TablePart tablePart, JoinType joinType) {
        this.tablePart = tablePart;
        this.joinType = joinType;
    }

    TableBlock(JoinType joinType, TablePart tablePart) {
        this.joinType = joinType;
        this.tablePart = tablePart;

    }

    @Override
    public final TablePart table() {
        return this.tablePart;
    }

    @Override
    public final SQLModifier jointType() {
        return this.joinType;
    }


    static TableBlock fromBlock(TablePart tablePart, String alias) {
        Objects.requireNonNull(tablePart);
        return new SimpleTableBlock(tablePart, alias);
    }


    static class SimpleTableBlock extends TableBlock {

        private final String alias;

        SimpleTableBlock(TablePart tablePart, String alias) {
            super(tablePart, JoinType.NONE);
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
