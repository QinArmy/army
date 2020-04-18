package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MySQLSelect extends Select {

    interface MySQLSelectSQLAble extends SelectSQLAble {

    }


    interface SelectPartAble<C> extends Select.SelectPartAble<C>, MySQLSelectSQLAble {

        FromAble<C> select(List<MySQLModifier> modifierList, String tableAlias, TableMeta<?> tableMeta);

        FromAble<C> select(List<MySQLModifier> modifierList, String subQueryAlias);

        <S extends SelectPart> FromAble<C> select(List<MySQLModifier> modifierList, List<S> selectPartList);

        <S extends SelectPart> FromAble<C> select(List<MySQLModifier> modifierList, Function<C, List<S>> function);
    }

    interface MySQLGroupByAble<C> extends GroupByAble<C>, MySQLSelectSQLAble {

        MySQLHavingAble<C> groupByAndRollUp(Expression<?> groupExp);

        MySQLHavingAble<C> groupByAndRollUp(Function<C, List<Expression<?>>> function);

        MySQLHavingAble<C> ifGroupByAndRollUp(Predicate<C> predicate, Function<C, MySQLGroup> expFunction);
    }

    interface MySQLHavingAble<C> extends LimitAble<C>, MySQLSelectSQLAble {

        LimitAble<C> having(IPredicate predicate);

        LimitAble<C> having(Function<C, List<IPredicate>> function);

        LimitAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        LimitAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);

    }


}
