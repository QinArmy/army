package io.army.criteria.impl;

import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Deprecated
final class MySQLFromTableBlock extends TableBlock implements _MySQLTableBlock {

    private final String alias;

    private final List<String> partitionList;

    List<MySQLIndexHint> indexHintList;

    MySQLFromTableBlock(TableMeta<?> table, String alias) {
        super(_JoinType.NONE, table);
        this.alias = alias;
        this.partitionList = Collections.emptyList();
    }

    MySQLFromTableBlock(TablePart tablePart, String alias, List<String> partitionList) {
        super(_JoinType.NONE, tablePart);
        this.alias = alias;
        this.partitionList = partitionList;
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public List<_Predicate> predicates() {
        return Collections.emptyList();
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
            indexHintList = CollectionUtils.unmodifiableList(indexHintList);
        }
        return indexHintList;
    }

}// MySQLFromTableBlock
