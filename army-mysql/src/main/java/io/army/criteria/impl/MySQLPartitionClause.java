package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class MySQLPartitionClause<C, PR> implements MySQLQuery.PartitionClause<C, PR> {

    final C criteria;

    /**
     * an unmodified list
     */
    List<String> partitionList;

    MySQLPartitionClause(@Nullable C criteria) {
        this.criteria = criteria;
    }

    @Override
    public final PR partition(String partitionName) {
        this.partitionList = Collections.singletonList(partitionName);
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2) {
        this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
        return (PR) this;
    }

    @Override
    public final PR partition(final List<String> partitionNameList) {
        if (partitionNameList.size() == 0) {
            throw new CriteriaException("partitionNameList must not empty.");
        }
        this.partitionList = _CollectionUtils.asUnmodifiableList(partitionNameList);
        return (PR) this;
    }

    @Override
    public final PR partition(Supplier<List<String>> supplier) {
        return this.partition(supplier.get());
    }

    @Override
    public final PR partition(Function<C, List<String>> function) {
        return this.partition(function.apply(this.criteria));
    }

    @Override
    public final PR ifPartition(Supplier<List<String>> supplier) {
        final List<String> list;
        list = supplier.get();
        if (!_CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }


}
