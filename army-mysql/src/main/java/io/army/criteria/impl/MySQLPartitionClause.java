package io.army.criteria.impl;

import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class MySQLPartitionClause<C, PR> implements MySQLQuery._PartitionClause<C, PR> {

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
    public final PR partition(String partitionName1, String partitionNam2, String partitionNam3) {
        this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2, partitionNam3);
        return (PR) this;
    }

    @Override
    public final PR partition(Supplier<List<String>> supplier) {
        this.partitionList = MySQLUtils.asStringList(supplier.get(), MySQLUtils::partitionListIsEmpty);
        return (PR) this;
    }

    @Override
    public final PR partition(Function<C, List<String>> function) {
        this.partitionList = MySQLUtils.asStringList(function.apply(this.criteria), MySQLUtils::partitionListIsEmpty);
        return (PR) this;
    }

    @Override
    public final PR partition(Consumer<List<String>> consumer) {
        final List<String> partitionList = new ArrayList<>();
        consumer.accept(partitionList);
        if (partitionList.size() == 0) {
            throw MySQLUtils.partitionListIsEmpty();
        }
        this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Supplier<List<String>> supplier) {
        final List<String> list;
        list = supplier.get();
        if (list != null && list.size() > 0) {
            this.partitionList = MySQLUtils.asStringList(list, MySQLUtils::partitionListIsEmpty);
        }
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (list != null && list.size() > 0) {
            this.partitionList = MySQLUtils.asStringList(list, , MySQLUtils::partitionListIsEmpty);
        }
        return (PR) this;
    }


}
