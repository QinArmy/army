package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSelectAble;
import io.army.dialect.SQLDialect;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class SelectAbleImpl<C> extends AbstractSQLAble implements
        SelectAble.DistinctAble<C>, SelectAble.FromAble<C>, SelectAble.JoinAble<C>,
        SelectAble.OnAble<C>,SelectAble.WhereAndAble<C>, InnerSelectAble {

    private final C criteria;


    SelectAbleImpl(C criteria) {
        this.criteria = criteria;
    }

    /*################################## blow DistinctAble method ##################################*/

    @Override
    public SelectAble.SelectListAble<C> modifier(DistinctModifier distinctModifier) {
        return null;
    }

    /*################################## blow SelectListAble method ##################################*/

    @Override
    public <T extends IDomain> SelectAble.FromAble<C> select(TableMeta<T> tableMeta) {
        return null;
    }

    @Override
    public SelectAble.FromAble<C> select(List<Selection> selectionList) {
        return null;
    }

    @Override
    public SelectAble.FromAble<C> select(Function<C, List<Selection>> function) {
        return null;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public <T extends IDomain> SelectAble.JoinAble<C> from(TableMeta<T> tableMeta,String tableAlias) {
        return null;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public <T extends IDomain> OnAble<C> join(TableMeta<T> tableMeta, String tableAlias) {
        return null;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public JoinAble<C> on(List<IPredicate> predicateList) {
        return null;
    }

    @Override
    public JoinAble<C> on(IPredicate predicate) {
        return null;
    }

    @Override
    public JoinAble<C> on(Function<C, List<IPredicate>> function) {
        return null;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public GroupByAble<C> where(List<IPredicate> predicateList) {
        return null;
    }

    @Override
    public GroupByAble<C> where(Function<C, List<IPredicate>> function) {
        return null;
    }

    @Override
    public WhereAndAble<C> where(IPredicate predicate) {
        return null;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public WhereAndAble<C> and(IPredicate predicate) {
        return null;
    }

    @Override
    public WhereAndAble<C> and(Function<C, IPredicate> function) {
        return null;
    }

    @Override
    public WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        return null;
    }

    @Override
    public WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        return null;
    }

    /*################################## blow GroupByAble method ##################################*/

    @Override
    public HavingAble<C> groupBy(SortExpression<?> groupExp) {
        return null;
    }

    @Override
    public HavingAble<C> groupBy(Function<C, List<SortExpression<?>>> function) {
        return null;
    }

    @Override
    public HavingAble<C> ifGroupBy(Predicate<C> predicate, SortExpression<?> groupExp) {
        return null;
    }

    @Override
    public HavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction) {
        return null;
    }

    /*################################## blow HavingAble method ##################################*/

    @Override
    public OrderByAble<C> having(List<IPredicate> predicateList) {
        return null;
    }

    @Override
    public OrderByAble<C> having(Function<C, List<IPredicate>> function) {
        return null;
    }

    @Override
    public OrderByAble<C> having(IPredicate predicate) {
        return null;
    }

    @Override
    public OrderByAble<C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList) {
        return null;
    }

    @Override
    public OrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        return null;
    }

    @Override
    public OrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        return null;
    }

    /*################################## blow OrderByAble method ##################################*/

    @Override
    public LimitAble<C> orderBy(SortExpression<?> groupExp) {
        return null;
    }

    @Override
    public LimitAble<C> orderBy(Function<C, List<SortExpression<?>>> function) {
        return null;
    }

    @Override
    public LimitAble<C> ifOrderBy(Predicate<C> predicate, SortExpression<?> groupExp) {
        return null;
    }

    @Override
    public LimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction) {
        return null;
    }

    /*################################## blow LimitAble method ##################################*/

    @Override
    public  LockAble<C> limit(int rowCount) {
        return null;
    }

    @Override
    public  LockAble<C> limit(int offset, int rowCount) {
        return null;
    }

    @Override
    public  LockAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        return null;
    }

    @Override
    public  LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        return null;
    }

    /*################################## blow LockAble method ##################################*/

    @Override
    public SelectAble lock(LockMode lockMode) {
        return null;
    }

    @Override
    public SelectAble lock(Function<C, LockMode> function) {
        return null;
    }

    @Override
    public SelectAble ifLck(Predicate<C> predicate, LockMode lockMode) {
        return null;
    }

    @Override
    public SelectAble ifLck(Predicate<C> predicate, Function<C, LockMode> function) {
        return null;
    }

    /*################################## blow AbstractSQLAble method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect, Visible visible) {
       /* SessionFactory sessionFactory = createSessionFactory(tableMeta.schema(), sqlDialect);
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().update(this, visible);
        return printSQL(sqlWrapperList, sessionFactory.dialect());*/
        return "";
    }
}
