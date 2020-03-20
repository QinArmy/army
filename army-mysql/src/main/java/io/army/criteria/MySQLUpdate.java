package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MySQLUpdate extends Update {

    interface MySQLUpdateSQLAble extends UpdateSQLAble {

    }


    interface MySQLSingleUpdateAble<C> extends SingleUpdateAble<C>, MySQLUpdateSQLAble {

        MySQLSetAble<C> update(TableMeta<?> tableMeta, String tableAlias);

        MySQLSetAble<C> update(List<MySQLModifier> modifierList, TableMeta<?> tableMeta, String tableAlias);
    }

    interface MySQLMultiUpdateAble<C> extends MultiUpdateAble<C>, MySQLUpdateSQLAble {

        JoinAble<C> update(List<MySQLModifier> modifierList, TableMeta<?> tableMeta, String tableAlias);

    }

    interface MySQLSetAble<C> extends SetAble<C>, MySQLUpdateSQLAble {

        <F> MySQLWhereAble<C> set(FieldMeta<? extends IDomain, F> target, F value);

        <F> MySQLWhereAble<C> set(FieldMeta<? extends IDomain, F> target, Expression<F> valueExp);

        <F> MySQLWhereAble<C> set(FieldMeta<? extends IDomain, F> target, Function<C, Expression<?>> function);

    }

    interface MySQLWhereAble<C> extends WhereAble<C>, MySQLUpdateSQLAble {

        <F> MySQLWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, F value);

        <F> MySQLWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, Expression<F> valueExp);

        <F> MySQLWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
                , Function<C, Expression<?>> valueExpFunction);

        MySQLOrderByAble<C> where(List<IPredicate> predicateList);

        MySQLOrderByAble<C> where(Function<C, List<IPredicate>> function);

        MySQLWhereAndAble<C> where(IPredicate predicate);
    }

    interface MySQLWhereAndAble<C> extends MySQLOrderByAble<C>, WhereAndAble<C> {

        MySQLWhereAndAble<C> and(IPredicate predicate);

        MySQLWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        MySQLWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface MySQLOrderByAble<C> extends MySQLLimitAble<C> {

        MySQLLimitAble<C> orderBy(Expression<?> orderExp);

        MySQLLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        MySQLLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        MySQLLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface MySQLLimitAble<C> extends UpdateAble, MySQLUpdateSQLAble {

        UpdateAble limit(int rowCount);

        UpdateAble limit(Function<C, Integer> function);

        UpdateAble ifLimit(Predicate<C> predicate, int rowCount);

        UpdateAble ifLimit(Predicate<C> predicate, Function<C, Integer> function);
    }


}
