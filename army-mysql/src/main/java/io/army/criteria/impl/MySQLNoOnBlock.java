package io.army.criteria.impl;

import io.army.criteria.TableItem;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;

import java.util.Collections;
import java.util.List;

final class MySQLNoOnBlock extends TableBlock.NoOnTableBlock implements _MySQLTableBlock {


    private final List<String> partitionList;

    List<MySQLIndexHint> indexHintList;


    MySQLNoOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
        super(joinType, tableItem, alias);
        this.partitionList = Collections.emptyList();
    }

    MySQLNoOnBlock(_JoinType joinType, TableItem tableItem, String alias, List<String> partitionList) {
        super(joinType, tableItem, alias);
        this.partitionList = partitionList;
    }

    @Override
    public List<String> partitionList() {
        return this.partitionList;
    }

    @Override
    public List<? extends _IndexHint> indexHintList() {
        List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = Collections.emptyList();
        } else {
            indexHintList = Collections.unmodifiableList(indexHintList);
        }
        return indexHintList;
    }


}
