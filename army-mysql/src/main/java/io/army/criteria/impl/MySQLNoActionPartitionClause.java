package io.army.criteria.impl;

import io.army.criteria.mysql.MySQLQuery;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


@Deprecated
@SuppressWarnings("unchecked")
abstract class MySQLNoActionPartitionClause<C, PR> implements MySQLQuery._PartitionClause2<C, PR> {


    @Override
    public final PR partition(String partitionName) {
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2) {
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2, String partitionNam3) {
        return (PR) this;
    }

    @Override
    public final PR partition(Consumer<Consumer<String>> consumer) {
        return (PR) this;
    }

    @Override
    public final PR partition(BiConsumer<C, Consumer<String>> consumer) {
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Consumer<Consumer<String>> consumer) {
        return (PR) this;
    }

    @Override
    public final PR ifPartition(BiConsumer<C, Consumer<String>> consumer) {
        return (PR) this;
    }


}
