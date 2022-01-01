package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.criteria.TablePart;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect._DialectUtils;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLPartitionOnClause<C, PR, AR, OR, IR, WP, WR> extends MySQLIndexHintClaus<C, OR, IR, WP, WR>
        implements MySQLQuery.PartitionClause<C, PR>, Statement.AsClause<AR> {

    private String alias;

    private List<String> partitionList;

    MySQLPartitionOnClause(TablePart tablePart, JoinType joinType, OR query) {
        super(tablePart, joinType, query);
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
            throw new CriteriaException("partition must not empty.");
        }
        final List<String> list = new ArrayList<>(2);
        list.addAll(partitionNameList);
        this.partitionList = Collections.unmodifiableList(list);
        return (PR) this;
    }

    @Override
    public final PR partition(Supplier<List<String>> supplier) {
        return this.partition(supplier.get());
    }

    @Override
    public final PR partition(Function<C, List<String>> function) {
        return this.partition(function.apply(this.getCriteria()));
    }

    @Override
    public final PR ifPartition(Supplier<List<String>> supplier) {
        final List<String> list;
        list = supplier.get();
        if (!CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.getCriteria());
        if (!CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }

    @Override
    public final AR as(String alias) {
        _DialectUtils.validateTableAlias(alias);
        this.alias = alias;
        return (AR) this;
    }

    @Override
    public final String alias() {
        final String alias = this.alias;
        assert alias != null;
        return alias;
    }

    @Override
    public final List<String> partitionList() {
        List<String> partitionList = this.partitionList;
        if (partitionList == null) {
            partitionList = Collections.emptyList();
        }
        return partitionList;
    }


}
