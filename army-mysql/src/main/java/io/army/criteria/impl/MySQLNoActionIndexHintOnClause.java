package io.army.criteria.impl;

import io.army.criteria.mysql.MySQLQuery;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Deprecated

@SuppressWarnings("unchecked")
abstract class MySQLNoActionIndexHintOnClause<C, IR, IC, OR> extends NoActionOnClause<C, OR>
        implements MySQLQuery._IndexHintClause<C, IR, IC>, MySQLQuery._IndexPurposeClause<C, IC> {

    MySQLNoActionIndexHintOnClause(OR stmt) {
        super(stmt);
    }

    @Override
    public final IR useIndex() {
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        return (IR) this;
    }

    @Override
    public final IC useIndex(List<String> indexList) {
        return (IC) this;
    }

    @Override
    public final IC ignoreIndex(List<String> indexList) {
        return (IC) this;
    }

    @Override
    public final IC forceIndex(List<String> indexList) {
        return (IC) this;
    }

    @Override
    public final IC ifUseIndex(Function<C, List<String>> function) {
        return (IC) this;
    }

    @Override
    public final IC ifIgnoreIndex(Function<C, List<String>> function) {
        return (IC) this;
    }

    @Override
    public final IC ifForceIndex(Function<C, List<String>> function) {
        return (IC) this;
    }

    @Override
    public final IC forJoin(List<String> indexList) {
        return (IC) this;
    }

    @Override
    public final IC forOrderBy(List<String> indexList) {
        return (IC) this;
    }

    @Override
    public final IC forGroupBy(List<String> indexList) {
        return (IC) this;
    }

    @Override
    public final IC forJoin(Function<C, List<String>> function) {
        return (IC) this;
    }

    @Override
    public final IC forOrderBy(Function<C, List<String>> function) {
        return (IC) this;
    }

    @Override
    public final IC forGroupBy(Function<C, List<String>> function) {
        return (IC) this;
    }


}
