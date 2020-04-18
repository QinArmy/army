package io.army.criteria.oracle;

import io.army.criteria.*;
import io.army.meta.FieldMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface OracleSelect extends Select {


    /*################################## blow interfaces ##################################*/

    interface OracleSelectSQLAble extends SelectSQLAble {

    }

    interface OracleSelectAble extends SelectAble, OracleSelectSQLAble {

        @Override
        OracleSelect asSelect();

    }


    interface OracleSelectPartAble<C> {

        <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        FromAble<C> select(Distinct distinct, SelectPart selectPart);

        FromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> FromAble<C> select(List<S> selectPartList);
    }


    interface OracleWhereAble<C> extends OracleHierarchicalAble<C> {

        OracleHierarchicalAble<C> where(List<IPredicate> predicateList);

        OracleHierarchicalAble<C> where(Function<C, List<IPredicate>> function);

        OracleWhereAndAble<C> where(IPredicate predicate);

    }


    interface OracleWhereAndAble<C> extends OracleHierarchicalAble<C> {

        OracleWhereAndAble<C> and(IPredicate predicate);

        OracleWhereAndAble<C> and(Function<C, IPredicate> function);

        OracleWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        OracleWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface OracleHierarchicalAble<C> extends OracleGroupByAble<C> {

        OracleHierarchicalStartClause<C> connectBy(IPredicate predicate);

        OracleHierarchicalStartClause<C> connectBy(Function<C, List<IPredicate>> function);

        OracleHierarchicalStartClause<C> connectByNoCycle(IPredicate predicate);

        OracleHierarchicalStartClause<C> connectByNoCycle(Function<C, List<IPredicate>> function);

        OracleHierarchicalConnectClause<C> startWith(IPredicate predicate);

        OracleHierarchicalConnectClause<C> startWith(Function<C, List<IPredicate>> function);

        OracleHierarchicalStartClause<C> ifConnectBy(Predicate<C> test, IPredicate predicate);

        OracleHierarchicalStartClause<C> ifConnectBy(Predicate<C> test, Function<C, List<IPredicate>> function);

        OracleHierarchicalStartClause<C> ifConnectByNoCycle(Predicate<C> test, IPredicate predicate);

        OracleHierarchicalStartClause<C> ifConnectByNoCycle(Predicate<C> test, Function<C, List<IPredicate>> function);

        OracleHierarchicalConnectClause<C> ifStartWith(Predicate<C> test, IPredicate predicate);

        OracleHierarchicalConnectClause<C> ifStartWith(Predicate<C> test, Function<C, List<IPredicate>> function);

    }

    interface OracleHierarchicalConnectClause<C> extends OracleSelectSQLAble {

        OracleGroupByAble<C> connectBy(IPredicate predicate);

        OracleGroupByAble<C> connectBy(Function<C, List<IPredicate>> function);

        OracleGroupByAble<C> connectByNoCycle(IPredicate predicate);

        OracleGroupByAble<C> connectByNoCycle(Function<C, List<IPredicate>> function);
    }

    interface OracleHierarchicalStartClause<C> extends OracleGroupByAble<C> {

        OracleGroupByAble<C> startWith(IPredicate predicate);

        OracleGroupByAble<C> startWith(Function<C, List<IPredicate>> function);

        OracleGroupByAble<C> ifStartWith(Predicate<C> test, IPredicate predicate);

        OracleGroupByAble<C> ifStartWith(Predicate<C> test, Function<C, List<IPredicate>> function);

    }


    interface OracleGroupByAble<C> extends OracleModelAble<C> {

        OracleModelAble<C> groupBy(Expression<?> groupExp);


        OracleModelAble<C> groupBy(List<Expression<?>> groupExpList);


        OracleModelAble<C> groupBy(Function<C, List<Expression<?>>> function);


        OracleModelAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);


        OracleModelAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface OracleModelAble<C> extends OracleOrderByAble<C> {

        OracleOrderByAble<C> model(Function<C, OracleModel> function);

        OracleOrderByAble<C> ifModel(Predicate<C> predicate, Function<C, OracleModel> function);
    }


    interface OracleSetAble<C> extends OracleSetClause<C>, OracleOrderByClause<C> {

    }


    interface OracleSetClause<C> extends OracleSelectAble {

        OracleSetAble<C> brackets();

        <S extends Select> OracleSetAble<C> union(Function<C, S> function);

        <S extends Select> OracleSetAble<C> unionAll(Function<C, S> function);

        <S extends Select> OracleSetAble<C> unionDistinct(Function<C, S> function);

        <S extends Select> OracleSetAble<C> intersect(Function<C, S> function);

        <S extends Select> OracleSetAble<C> intersectAll(Function<C, S> function);

        <S extends Select> OracleSetAble<C> intersectDistinct(Function<C, S> function);

        <S extends Select> OracleSetAble<C> minus(Function<C, S> function);

        <S extends Select> OracleSetAble<C> minusAll(Function<C, S> function);

        <S extends Select> OracleSetAble<C> minusDistinct(Function<C, S> function);

    }

    interface OracleOrderByAble<C> extends OracleOrderByClause<C>, OracleLimitAble<C> {

        @Override
        OracleLimitAble<C> orderBy(Expression<?> orderExp);

        @Override
        OracleLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        @Override
        OracleLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        @Override
        OracleLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface OracleOrderByClause<C> extends OracleLimitClause<C> {

        OracleLimitClause<C> orderBy(Expression<?> orderExp);

        OracleLimitClause<C> orderBy(Function<C, List<Expression<?>>> function);

        OracleLimitClause<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        OracleLimitClause<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }


    interface OracleLimitClause<C> extends OracleSetClause<C> {

        OracleSetClause<C> limit(int rowCount);

        OracleSetClause<C> limit(int offset, int rowCount);

        OracleSetClause<C> limit(Function<C, Pair<Integer, Integer>> function);

        OracleSetClause<C> ifLimit(Predicate<C> predicate, int rowCount);

        OracleSetClause<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        OracleSetClause<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);

    }


    interface OracleLimitAble<C> extends OracleLimitClause<C>, OracleLockAble<C> {


    }


    interface OracleLockAble<C> extends OracleSelectAble {

        OracleLockOfColumnsAble<C> forUpdate();
    }


    interface OracleLockOfColumnsAble<C> extends OracleLockOptionAble<C> {

        OracleLockOptionAble<C> of(List<FieldMeta<?, ?>> columnList);

        OracleLockOptionAble<C> of(Function<C, List<FieldMeta<?, ?>>> function);

    }

    interface OracleLockOptionAble<C> extends OracleSelectAble {

        OracleSelectAble noWait();

        OracleSelectAble wait(int waitSeconds);

        OracleSelectAble wait(Function<C, Integer> function);

        OracleSelectAble skipLocked();

    }

    /*################################## blow  Model clause interface ##################################*/

    interface OracleCellRefNavOptionAble<C> extends OracleCellRefUniqueOptionAble<C> {

        OracleCellRefUniqueOptionAble<C> ignoreNav();

        OracleCellRefUniqueOptionAble<C> keepNav();
    }

    interface OracleCellRefUniqueOptionAble<C> extends OracleReturnRowAble<C> {

        OracleReturnRowAble<C> uniqueDimension();

        OracleReturnRowAble<C> uniqueSingleReference();
    }

    interface OracleReturnRowAble<C> extends OracleModelReferenceAble<C> {

        OracleModelReferenceAble<C> returnUpdatedRows();

        OracleModelReferenceAble<C> returnAllRows();
    }

    interface OracleModelReferenceAble<C> extends OracleMainModelAble<C> {

        OracleMainModelAble<C> referenceModes(Function<C, List<OracleReferenceModel>> function);

    }

    interface OracleMainModelAble<C> extends OracleSelectSQLAble {

        OracleOrderByAble<C> main(Function<C, OracleMainModel> function);

    }

    interface OracleModel {

    }

    /*################################## blow Reference Model clause interface ##################################*/


    interface OracleReferenceModelNameAble<C> {

        OracleReferenceModelOnAble<C> reference(String refModelName);

    }

    interface OracleReferenceModelOnAble<C> {

        <S extends SubQuery> OracleRefModelCellRefNavOptionAble<C> on(Function<C, S> subQueryFunc
                , Function<C, OracleModelColumns> modelColFunc);

    }

    interface OracleRefModelCellRefNavOptionAble<C> extends OracleRefModelCellRefUniqueOptionAble<C> {

        OracleRefModelCellRefUniqueOptionAble<C> ignoreNav();

        OracleRefModelCellRefUniqueOptionAble<C> keepNav();

    }

    interface OracleRefModelCellRefUniqueOptionAble<C> extends OracleReferenceModeAble {

        OracleReferenceModeAble uniqueDimension();

        OracleReferenceModeAble uniqueSingleReference();
    }

    interface OracleReferenceModeAble {

        OracleReferenceModel asReferenceModel();

    }

    interface OracleReferenceModel {

    }

    interface OracleModelColumns {

    }

    /*################################## blow Main Model clause interface ##################################*/

    interface OracleMainModel {

    }
}
