package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.CteTableItem;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class TableBlock implements _TableBlock {

    final _JoinType joinType;

    final Object tableItem;

    final String alias;

    TableBlock(_JoinType joinType, Object tableItem, String alias) {
        Objects.requireNonNull(alias);
        if (!(tableItem instanceof CteTableItem) && !_StringUtils.hasText(alias)) {
            String m = String.format("Non-%s alias must have text.", CteTableItem.class.getSimpleName());
            throw new CriteriaException(m);
        }
        this.joinType = joinType;
        this.tableItem = tableItem;
        this.alias = alias;

    }

    @Override
    public final Object tableItem() {
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

        public NoOnTableBlock(_JoinType joinType, Object tableItem, String alias) {
            super(joinType, tableItem, alias);
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    break;
                default:
                    throw _Exceptions.castCriteriaApi();
            }

        }

        @Override
        public final List<_Predicate> predicates() {
            return Collections.emptyList();
        }

    }//NoOnTableBlock


}
