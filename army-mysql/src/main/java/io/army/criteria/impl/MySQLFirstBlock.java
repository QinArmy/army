package io.army.criteria.impl;

import io.army.criteria.TableItem;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

final class MySQLFirstBlock<C, OR> extends OnClauseTableBlock<C, OR> implements _MySQLTableBlock {


    private final List<String> partitionList;

    List<MySQLIndexHint> indexHintList;

    private final OR stmt;

    /**
     * @param stmt the implementation of {@link CriteriaContextSpec}
     */
    MySQLFirstBlock(TableMeta<?> table, String alias, List<String> partitionList, OR stmt) {
        super(_JoinType.NONE, table, alias);
        this.partitionList = CollectionUtils.unmodifiableList(partitionList);
        this.stmt = stmt;
    }

    MySQLFirstBlock(TableItem tableItem, String alias, OR stmt) {
        super(_JoinType.NONE, tableItem, alias);
        this.partitionList = Collections.emptyList();
        this.stmt = stmt;
    }

    @Override
    CriteriaContext getCriteriaContext() {
        return ((CriteriaContextSpec) this.stmt).getCriteriaContext();
    }

    @Override
    OR endOnClause() {
        return this.stmt;
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
