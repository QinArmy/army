package io.army.criteria.impl;

import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;

import java.util.Collections;
import java.util.List;

final class MySQLTableBlock extends TableBlock implements _MySQLTableBlock {

    private final List<String> partitionList;

    private final String alias;

    private final List<_IndexHint> indexHintList;


    private MySQLTableBlock(TablePart tablePart, JoinType joinType
            , List<String> partitionList
            , String alias, List<_IndexHint> indexHintList) {
        super(tablePart, joinType);
        this.partitionList = partitionList;
        this.alias = alias;
        this.indexHintList = indexHintList;
    }

    @Override
    public String alias() {
        return this.alias;
    }


    @Override
    public List<_Predicate> predicates() {
        return Collections.emptyList()
    }

    @Override
    public List<String> partitionList() {
        return this.partitionList;
    }

    @Override
    public List<? extends _IndexHint> indexHintList() {
        return this.indexHintList;
    }
}
