package io.army.criteria.impl;

import io.army.criteria.mysql.MySQLQuery;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
@SuppressWarnings("unchecked")
abstract class MySQLNoActionPartitionClause<C, PR> implements MySQLQuery._PartitionClause<C, PR> {


    @Override
    public final PR partition(String partitionName) {
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2) {
        return (PR) this;
    }


    @Override
    public final PR partition(Supplier<List<String>> supplier) {
        return (PR) this;
    }

    @Override
    public final PR partition(Function<C, List<String>> function) {
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Supplier<List<String>> supplier) {
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        return (PR) this;
    }


}
