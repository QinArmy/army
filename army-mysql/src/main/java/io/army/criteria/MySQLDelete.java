package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MySQLDelete extends Delete {

    interface MySQLDeleteSQLAble extends DeleteSQLAble {

    }

    interface MySQLSingleDeleteAble<C> extends SingleDeleteAble<C>, MySQLDeleteSQLAble {

        MySQLNoJoinFromAble<C> delete(List<MySQLModifier> modifierList);

        @Override
        MySQLNoJoinFromAble<C> delete();
    }

    interface MySQLMultiDeleteAble<C> extends MultiDeleteAble<C>, MySQLDeleteSQLAble {

        FromAble<C> delete(List<MySQLModifier> modifierList);

    }

    interface MySQLNoJoinFromAble<C> extends NoJoinFromAble<C>, MySQLDeleteSQLAble {

        @Override
        MySQLWhereAble<C> from(TableMeta<?> tableMeta);

    }

    interface MySQLWhereAble<C> extends WhereAble<C>, MySQLDeleteSQLAble {

        @Override
        MySQLOrderByAble<C> where(List<IPredicate> predicateList);

        @Override
        MySQLOrderByAble<C> where(Function<C, List<IPredicate>> function);

        @Override
        MySQLWhereAndAble<C> where(IPredicate predicate);
    }

    interface MySQLWhereAndAble<C> extends WhereAndAble<C>, MySQLOrderByAble<C> {

        @Override
        MySQLWhereAndAble<C> and(IPredicate predicate);

        @Override
        MySQLWhereAndAble<C> and(Function<C, IPredicate> function);

        @Override
        MySQLWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        @Override
        MySQLWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }

    interface MySQLOrderByAble<C> extends MySQLLimitAble<C> {

        MySQLLimitAble<C> orderBy(Expression<?> orderExp);

        MySQLLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        MySQLLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        MySQLLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }

    interface MySQLLimitAble<C> extends DeleteAble, MySQLDeleteSQLAble {

        DeleteAble limit(int rowCount);

        DeleteAble limit(Function<C, Integer> function);

        DeleteAble ifLimit(Predicate<C> predicate, int rowCount);

        DeleteAble ifLimit(Predicate<C> predicate, Function<C, Integer> function);
    }


}
