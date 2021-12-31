package io.army.criteria.impl;

import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;

import java.util.Collections;
import java.util.List;

abstract class TableBlock implements _TableBlock {

    final TablePart tablePart;

    final String alias;

    final JoinType joinType;

    TableBlock(TablePart tablePart, String alias, JoinType joinType) {
        this.tablePart = tablePart;
        this.alias = alias;
        this.joinType = joinType;
    }

    @Override
    public final TablePart table() {
        return this.tablePart;
    }

    @Override
    public final String alias() {
        return this.alias;
    }

    @Override
    public final SQLModifier jointType() {
        return this.joinType;
    }


    static final class FromTableBlock extends TableBlock {

        FromTableBlock(TablePart tablePart, String alias) {
            super(tablePart, alias, JoinType.NONE);
        }

        @Override
        public List<_Predicate> predicates() {
            return Collections.emptyList();
        }

    }


}
