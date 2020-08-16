package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select.html">MySQL 5.7 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/union.html">MySQL 5.7 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Select extends Select {


    interface MySQLSelectPartAble<C> extends SelectSQLAble {

        <S extends SelectPart> MySQLFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> MySQLFromAble<C> select(Function<C, List<S>> function);

        MySQLFromAble<C> select(Distinct distinct, SelectPart selectPart);

        MySQLFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> MySQLFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> MySQLFromAble<C> select(List<S> selectPartList);
    }


    interface MySQLFromAble<C> extends UnionClause<C> {

        MySQLTableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        MySQLJoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface MySQLAfterFromIndexHintAble<C> extends MySQLJoinAble<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLJoinAble<C> indexHintList(Function<C, List<MySQLIndexHint>> function);
    }

    interface MySQLTableRouteJoinAble<C> extends MySQLAfterFromIndexHintAble<C> {

        MySQLAfterFromIndexHintAble<C> route(int databaseIndex, int tableIndex);

        MySQLAfterFromIndexHintAble<C> route(int tableIndex);
    }


    interface MySQLOnAble<C> extends SelectSQLAble {

        MySQLJoinAble<C> on(List<IPredicate> predicateList);

        MySQLJoinAble<C> on(IPredicate predicate);

        MySQLJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface MySQLAfterJoneIndexHintAble<C> extends MySQLOnAble<C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLOnAble<C> indexHintList(Function<C, List<MySQLIndexHint>> function);
    }

    interface MySQLTableRouteOnAble<C> extends MySQLAfterJoneIndexHintAble<C> {

        MySQLOnAble<C> route(int databaseIndex, int tableIndex);

        MySQLOnAble<C> route(int tableIndex);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
     */
    interface MySQLJoinAble<C> extends MySQLWhereAble<C> {

        MySQLTableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnAble<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnAble<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface MySQLWhereAble<C> extends MySQLGroupByAble<C> {

        MySQLGroupByAble<C> where(List<IPredicate> predicateList);

        MySQLGroupByAble<C> where(Function<C, List<IPredicate>> function);

        MySQLWhereAndAble<C> where(IPredicate predicate);
    }

    interface MySQLWhereAndAble<C> extends MySQLGroupByAble<C> {

        MySQLWhereAndAble<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        MySQLWhereAndAble<C> ifAnd(@Nullable IPredicate predicate);

        MySQLWhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }


    interface MySQLGroupByAble<C> extends MySQLOrderByAble<C> {

        MySQLWithRollUpAble<C> groupBy(SortPart sortPart);

        MySQLWithRollUpAble<C> groupBy(List<SortPart> sortPartList);

        MySQLWithRollUpAble<C> groupBy(Function<C, List<SortPart>> function);
    }

    interface MySQLWithRollUpAble<C> extends MySQLHavingAble<C> {

        MySQLHavingAble<C> withRollUp();
    }

    interface MySQLHavingAble<C> extends MySQLOrderByAble<C> {

        MySQLOrderByAble<C> having(IPredicate predicate);

        MySQLOrderByAble<C> having(List<IPredicate> predicateList);

        MySQLOrderByAble<C> having(Function<C, List<IPredicate>> function);
    }


    interface MySQLOrderByAble<C> extends MySQLOrderByClause<C>, MySQLLimitAble<C> {

        @Override
        MySQLLimitAble<C> orderBy(SortPart sortPart);

        @Override
        MySQLLimitAble<C> orderBy(List<SortPart> sortPartList);

        @Override
        MySQLLimitAble<C> orderBy(Function<C, List<SortPart>> function);
    }


    interface MySQLLimitAble<C> extends MySQLLimitClause<C>, MySQLLockAble<C> {

        @Override
        MySQLLockAble<C> limit(int rowCount);

        @Override
        MySQLLockAble<C> limit(int offset, int rowCount);

        @Override
        MySQLLockAble<C> ifLimit(Function<C, LimitOption> function);

        @Override
        MySQLLockAble<C> ifLimit(Predicate<C> test, int rowCount);

        @Override
        MySQLLockAble<C> ifLimit(Predicate<C> test, int offset, int rowCount);

    }

    interface MySQLLockAble<C> extends SelectAble, UnionClause<C> {

        SelectAble forUpdate();

        SelectAble lockInShareMode();

    }


    interface MySQLUnionAble<C> extends UnionClause<C>, MySQLOrderByClause<C> {

    }


    interface MySQLOrderByClause<C> extends MySQLLimitClause<C> {

        MySQLLimitClause<C> orderBy(SortPart sortPart);

        MySQLLimitClause<C> orderBy(List<SortPart> sortPartList);

        MySQLLimitClause<C> orderBy(Function<C, List<SortPart>> function);
    }

    interface MySQLLimitClause<C> extends SelectAble {

        SelectAble limit(int rowCount);

        SelectAble limit(int offset, int rowCount);

        SelectAble ifLimit(Function<C, LimitOption> function);

        SelectAble ifLimit(Predicate<C> predicate, int rowCount);

        SelectAble ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }


}
