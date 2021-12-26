package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQL57Query;
import io.army.criteria.mysql.MySQL57IndexHint;
import io.army.criteria.mysql.MySQL57Query;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class AbstractMySQL57Query<Q extends MySQL57Query, C> implements MySQL57Query
        , MySQL57Query.SelectPart57Spec<Q, C>, MySQL57Query.From57Spec<Q, C>
        , MySQL57Query.MySQLTableRouteJoinSpec<Q, C>, MySQL57Query.Where57Spec<Q, C>
        , MySQL57Query.WhereAnd57Spec<Q, C>, MySQL57Query.GroupBy57Spec<Q, C>
        , MySQL57Query.WithRollUp57Spec<Q, C>, MySQL57Query.Having57Spec<Q, C>
        , _MySQL57Query {

    MySQLTableRouteOnSpec<Q, C> tableRouteOnSpec;


    private boolean enableIndexHint;

    private boolean withRollUp;

    AbstractMySQL57Query(C criteria) {
        super(criteria);

    }


    @Override
    public SessionMode sessionMode() {
        return super.sessionMode();
    }

    @Override
    public Union57Spec<Q, C> bracketsQuery() {
        return null;
    }

    @Override
    public Union57Spec<Q, C> union(Function<C, Q> function) {
        return null;
    }

    @Override
    public Union57Spec<Q, C> union(Supplier<Q> supplier) {
        return null;
    }

    @Override
    public SelectPartSpec<Q, C> union() {
        return null;
    }

    @Override
    public SelectPartSpec<Q, C> unionAll() {
        return null;
    }

    @Override
    public SelectPartSpec<Q, C> unionDistinct() {
        return null;
    }

    @Override
    public Union57Spec<Q, C> unionAll(Function<C, Q> function) {
        return null;
    }

    @Override
    public Union57Spec<Q, C> unionDistinct(Function<C, Q> function) {
        return null;
    }

    @Override
    public Union57Spec<Q, C> unionAll(Supplier<Q> function) {
        return null;
    }

    @Override
    public Union57Spec<Q, C> unionDistinct(Supplier<Q> function) {
        return null;
    }

    @Override
    _TableBlock createBlock(TableMeta tableMeta, String tableAlias, JoinType joinType) {
        return null;
    }

    @Override
    _TableBlock createBlock(Function function, String subQueryAlia, JoinType joinType) {
        return null;
    }

    @Override
    _TableBlock createBlock(Supplier supplier, String subQueryAlia, JoinType joinType) {
        return null;
    }

    @Override
    _TableBlock ifCreateBlock(Predicate predicate, TableMeta tableMeta, String tableAlias, JoinType joinType) {
        return null;
    }

    @Override
    _TableBlock ifCreateBlock(Function function, String subQueryAlia, JoinType joinType) {
        return null;
    }

    @Override
    _TableBlock ifCreateBlock(Supplier supplier, String subQueryAlia, JoinType joinType) {
        return null;
    }

    @Override
    void onAddTable(TableMeta tableMeta, String tableAlias) {

    }

    @Override
    void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {

    }

    @Override
    void internalAsSelect() {

    }

    @Override
    void internalClear() {

    }

    @Override
    boolean hasLockClause() {
        return false;
    }

    @Override
    public SQLModifier lockMode() {
        return null;
    }

    @Override
    public boolean groupByWithRollUp() {
        return false;
    }

    @Override
    public <S extends SelectPart> From57Spec<Q, C> select(Distinct distinct, Function<C, List<S>> function) {
        return null;
    }

    @Override
    public <S extends SelectPart> From57Spec<Q, C> select(Function<C, List<S>> function) {
        return null;
    }

    @Override
    public From57Spec<Q, C> select(Distinct distinct, SelectPart selectPart) {
        return null;
    }

    @Override
    public From57Spec<Q, C> select(SelectPart selectPart) {
        return null;
    }

    @Override
    public <S extends SelectPart> From57Spec<Q, C> select(Distinct distinct, List<S> selectPartList) {
        return null;
    }

    @Override
    public <S extends SelectPart> From57Spec<Q, C> select(List<S> selectPartList) {
        return null;
    }

    @Override
    public MySQLTableRouteJoinSpec<Q, C> from(TableMeta<?> table, String tableAlias) {
        return null;
    }

    @Override
    public Join57Spec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        return null;
    }

    @Override
    public Join57Spec<Q, C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function) {
        return null;
    }

    @Override
    public Join57Spec<Q, C> route(int databaseIndex, int tableIndex) {
        return null;
    }

    @Override
    public Join57Spec<Q, C> route(int tableIndex) {
        return null;
    }

    @Override
    public MySQLTableRouteOnSpec<Q, C> straightJoin(TableMeta<?> tableMeta, String tableAlias) {
        return null;
    }

    @Override
    public On57Spec<Q, C> straightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return null;
    }

    @Override
    public MySQLTableRouteOnSpec<Q, C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        return null;
    }

    @Override
    public On57Spec<Q, C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return null;
    }

    @Override
    public GroupBy57Spec<Q, C> where(Function<C, List<IPredicate>> function) {
        return null;
    }

    @Override
    public Having57Spec<Q, C> withRollUp() {
        return null;
    }

    @Override
    public Having57Spec<Q, C> withRollUp(Predicate<C> predicate) {
        return null;
    }

    @Override
    public Lock57Spec<Q, C> limit(int rowCount) {
        return null;
    }

    @Override
    public Lock57Spec<Q, C> limit(int offset, int rowCount) {
        return null;
    }

    @Override
    public Lock57Spec<Q, C> ifLimit(Predicate<C> predicate, int rowCount) {
        return null;
    }

    @Override
    public Lock57Spec<Q, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        return null;
    }

    @Override
    public QuerySpec<Q> forUpdate() {
        return null;
    }

    @Override
    public Lock57Spec<Q, C> ifForUpdate(Predicate<C> predicate) {
        return null;
    }

    @Override
    public QuerySpec<Q> lockInShareMode() {
        return null;
    }

    @Override
    public Lock57Spec<Q, C> ifLockInShareMode(Predicate<C> predicate) {
        return null;
    }
}
