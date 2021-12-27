package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildDomain;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 */
public interface MySQLUpdate extends Update, DialectStatement {

    interface SingleUpdateSpec<C> {

        <T extends IDomain> SingleIndexHintCommandSpec<T, C> update(SingleTableMeta<T> table, String tableAlias);

        <T extends IDomain> SinglePartitionSpec<T, C> update(SingleTableMeta<T> table);

        <P extends IDomain, T extends P> SingleIndexHintCommandSpec<P, C> update(ChildDomain<P, T> table, String tableAlias);

        <P extends IDomain, T extends P> SinglePartitionSpec<P, C> update(ChildDomain<P, T> table);
    }


    interface SinglePartitionSpec<T extends IDomain, C> extends SingleAsSpec<T, C> {

        SingleIndexHintCommandSpec<T, C> partition(String partitionName);

        SingleIndexHintCommandSpec<T, C> partition(String partitionName1, String partitionNam2);

        SingleIndexHintCommandSpec<T, C> partition(List<String> partitionNameList);

        SingleIndexHintCommandSpec<T, C> ifPartition(Function<C, List<String>> function);

    }


    interface SingleAsSpec<T extends IDomain, C> {

        SingleIndexHintCommandSpec<T, C> as(String tableAlias);
    }

    interface SingleIndexHintCommandSpec<T extends IDomain, C> extends SingleSetSpec<T, C>
            , SingleIndexHintCommandClause<T, C> {

    }


    interface SingleIndexHintCommandClause<T extends IDomain, C> {

        SingleIndexWordClause<T, C> use();

        SingleIndexWordClause<T, C> ignore();

        SingleIndexWordClause<T, C> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        SingleIndexWordClause<T, C> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        SingleIndexWordClause<T, C> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        SingleIndexWordClause<T, C> ifForce(Predicate<C> predicate);

    }


    interface SingleIndexWordClause<T extends IDomain, C> {

        SingleOrderByClause<T, C> index();

        SingleOrderByClause<T, C> key();

        SingleSetSpec<T, C> index(List<String> indexNameList);

        SingleSetSpec<T, C> key(List<String> indexNameList);

    }


    interface SingleOrderByClause<T extends IDomain, C> {

        SingleSetSpec<T, C> forOrderBy(List<String> indexNameList);

    }


    interface SingleSetSpec<T extends IDomain, C> {

        SingleWhereSpec<T, C> ifSet(List<FieldMeta<T, ?>> fieldList, List<Expression<?>> valueList);

        SingleWhereSpec<T, C> set(FieldMeta<T, ?> field, @Nullable Object value);

        SingleWhereSpec<T, C> set(FieldMeta<T, ?> field, Expression<?> value);

        SingleWhereSpec<T, C> set(FieldMeta<T, ?> field, Function<C, Expression<?>> function);

        SingleWhereSpec<T, C> set(FieldMeta<T, ?> field, Supplier<Expression<?>> supplier);

        SingleWhereSpec<T, C> setNull(FieldMeta<T, ?> field);

        SingleWhereSpec<T, C> setDefault(FieldMeta<T, ?> field);

        <F extends Number> SingleWhereSpec<T, C> setPlus(FieldMeta<T, ?> field, F value);

        <F extends Number> SingleWhereSpec<T, C> setPlus(FieldMeta<T, ?> field, Expression<?> value);

        <F extends Number> SingleWhereSpec<T, C> setMinus(FieldMeta<T, ?> field, F value);

        <F extends Number> SingleWhereSpec<T, C> setMinus(FieldMeta<T, ?> field, Expression<?> value);

        <F extends Number> SingleWhereSpec<T, C> setMultiply(FieldMeta<T, ?> field, F value);

        <F extends Number> SingleWhereSpec<T, C> setMultiply(FieldMeta<T, ?> field, Expression<?> value);

        <F extends Number> SingleWhereSpec<T, C> setDivide(FieldMeta<T, ?> field, F value);

        <F extends Number> SingleWhereSpec<T, C> setDivide(FieldMeta<T, ?> field, Expression<?> value);

        <F extends Number> SingleWhereSpec<T, C> setMod(FieldMeta<T, ?> field, F value);

        <F extends Number> SingleWhereSpec<T, C> setMod(FieldMeta<T, ?> field, Expression<?> value);

        SingleWhereSpec<T, C> ifSet(FieldMeta<T, ?> field, @Nullable Object value);

        SingleWhereSpec<T, C> ifSet(FieldMeta<T, ?> field, Function<C, Expression<?>> function);

        SingleWhereSpec<T, C> ifSet(FieldMeta<T, ?> field, Supplier<Expression<?>> supplier);

        <F extends Number> SingleWhereSpec<T, C> ifSetPlus(FieldMeta<T, ?> field, @Nullable F value);

        <F extends Number> SingleWhereSpec<T, C> ifSetMinus(FieldMeta<T, ?> field, @Nullable F value);

        <F extends Number> SingleWhereSpec<T, C> ifSetMultiply(FieldMeta<T, ?> field, @Nullable F value);

        <F extends Number> SingleWhereSpec<T, C> ifSetDivide(FieldMeta<T, ?> field, @Nullable F value);

        <F extends Number> SingleWhereSpec<T, C> ifSetMod(FieldMeta<T, ?> field, @Nullable F value);

    }


    interface SingleWhereSpec<T extends IDomain, C> extends MySQLUpdate.SingleSetSpec<T, C> {

        SingleWhereAndSpec<C> where(IPredicate predicate);

        OrderBySpec<C> where(List<IPredicate> predicateList);

        OrderBySpec<C> where(Function<C, List<IPredicate>> function);

        OrderBySpec<C> where(Supplier<List<IPredicate>> supplier);

    }

    interface SingleWhereAndSpec<C> extends OrderBySpec<C>, Update.WhereAndSpec<C> {

        @Override
        SingleWhereAndSpec<C> and(IPredicate predicate);

        @Override
        SingleWhereAndSpec<C> and(Function<C, IPredicate> function);

        @Override
        SingleWhereAndSpec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        @Override
        SingleWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        @Override
        SingleWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

        @Override
        SingleWhereAndSpec<C> ifAnd(Supplier<IPredicate> supplier);

    }


    interface OrderBySpec<C> extends MySQLUpdate.SingleLimitSpec<C> {

        SingleLimitSpec<C> orderBy(SortPart sortPart);

        SingleLimitSpec<C> orderBy(SortPart sortPart1, SortPart sortPart2);

        SingleLimitSpec<C> orderBy(List<SortPart> sortPartList);

        SingleLimitSpec<C> orderBy(Function<C, List<SortPart>> function);

        SingleLimitSpec<C> orderBy(Supplier<List<SortPart>> supplier);

        SingleLimitSpec<C> ifOrderBy(@Nullable SortPart sortPart);

        SingleLimitSpec<C> ifOrderBy(Supplier<List<SortPart>> supplier);

        SingleLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface SingleLimitSpec<C> extends Update.UpdateSpec {

        UpdateSpec limit(long rowCount);

        UpdateSpec limit(Function<C, Long> function);

        UpdateSpec limit(Supplier<Long> supplier);

        UpdateSpec ifLimit(Function<C, Long> function);

        UpdateSpec ifLimit(Supplier<Long> supplier);

    }


    /*################################## blow batch single update api interface ##################################*/


    interface BatchSingleUpdateSpec<C> {

        <T extends IDomain> BatchSingleIndexHintCommandSpec<T, C> update(SingleTableMeta<T> table, String tableAlias);

        <T extends IDomain> BatchSinglePartitionSpec<T, C> update(SingleTableMeta<T> table);

        <P extends IDomain, T extends P> BatchSingleIndexHintCommandSpec<P, C> update(ChildDomain<P, T> table, String tableAlias);

        <P extends IDomain, T extends P> BatchSinglePartitionSpec<P, C> update(ChildDomain<P, T> table);
    }


    interface BatchSinglePartitionSpec<T extends IDomain, C> extends BatchSingleAsSpec<T, C> {

        BatchSingleIndexHintCommandSpec<T, C> partition(String partitionName);

        BatchSingleIndexHintCommandSpec<T, C> partition(String partitionName1, String partitionNam2);

        BatchSingleIndexHintCommandSpec<T, C> partition(List<String> partitionNameList);

        BatchSingleIndexHintCommandSpec<T, C> ifPartition(Function<C, List<String>> function);

    }


    interface BatchSingleAsSpec<T extends IDomain, C> {

        BatchSingleIndexHintCommandSpec<T, C> as(String tableAlias);
    }

    interface BatchSingleIndexHintCommandSpec<T extends IDomain, C> extends BatchSingleSetSpec<T, C>
            , BatchSingleIndexHintCommandClause<T, C> {

    }


    interface BatchSingleIndexHintCommandClause<T extends IDomain, C> {

        BatchSingleIndexWordClause<T, C> use();

        BatchSingleIndexWordClause<T, C> ignore();

        BatchSingleIndexWordClause<T, C> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        BatchSingleIndexWordClause<T, C> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        BatchSingleIndexWordClause<T, C> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        BatchSingleIndexWordClause<T, C> ifForce(Predicate<C> predicate);

    }


    interface BatchSingleIndexWordClause<T extends IDomain, C> {

        BatchSingleOrderByClause<T, C> index();

        BatchSingleOrderByClause<T, C> key();

        BatchSingleSetSpec<T, C> index(List<String> indexNameList);

        BatchSingleSetSpec<T, C> key(List<String> indexNameList);

    }


    interface BatchSingleOrderByClause<T extends IDomain, C> {

        BatchSingleSetSpec<T, C> forOrderBy(List<String> indexNameList);
    }


    interface BatchSingleSetSpec<T extends IDomain, C> {

        /**
         * @see SQLs#namedParam(GenericField)
         */
        BatchSingleWhereSpec<T, C> set(List<FieldMeta<T, ?>> fieldList);

        BatchSingleWhereSpec<T, C> set(FieldMeta<T, ?> field, Expression<?> valueExp);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        BatchSingleWhereSpec<T, C> set(FieldMeta<T, ?> field);

        BatchSingleWhereSpec<T, C> setNull(FieldMeta<T, ?> field);

        BatchSingleWhereSpec<T, C> setDefault(FieldMeta<T, ?> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchSingleWhereSpec<T, C> setPlus(FieldMeta<T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchSingleWhereSpec<T, C> setMinus(FieldMeta<T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchSingleWhereSpec<T, C> setMultiply(FieldMeta<T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchSingleWhereSpec<T, C> setDivide(FieldMeta<T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchSingleWhereSpec<T, C> setMod(FieldMeta<T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        BatchSingleWhereSpec<T, C> ifSet(Function<C, List<FieldMeta<T, ?>>> function);

        BatchSingleWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<T, ?> field);

        BatchSingleWhereSpec<T, C> ifSet(FieldMeta<T, ?> filed, Function<C, Expression<?>> function);

    }


    interface BatchSingleWhereSpec<T extends IDomain, C> extends Update.BatchSetSpec<T, C> {

        BatchSingleWhereAndSpec<C> where(IPredicate predicate);

        BatchOrderBySpec<C> where(List<IPredicate> predicateList);

        BatchOrderBySpec<C> where(Function<C, List<IPredicate>> function);

        BatchOrderBySpec<C> where(Supplier<List<IPredicate>> supplier);

    }

    interface BatchSingleWhereAndSpec<C> extends MySQLUpdate.BatchOrderBySpec<C>, Update.WhereAndSpec<C> {

        @Override
        BatchSingleWhereAndSpec<C> and(IPredicate predicate);

        @Override
        BatchSingleWhereAndSpec<C> and(Function<C, IPredicate> function);

        @Override
        BatchSingleWhereAndSpec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        @Override
        BatchSingleWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        @Override
        BatchSingleWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

        @Override
        BatchSingleWhereAndSpec<C> ifAnd(Supplier<IPredicate> supplier);

    }

    interface BatchOrderBySpec<C> extends MySQLUpdate.BatchSingleLimitSpec<C> {

        BatchSingleLimitSpec<C> orderBy(SortPart sortPart);

        BatchSingleLimitSpec<C> orderBy(SortPart sortPart1, SortPart sortPart2);

        BatchSingleLimitSpec<C> orderBy(List<SortPart> sortPartList);

        BatchSingleLimitSpec<C> orderBy(Function<C, List<SortPart>> function);

        BatchSingleLimitSpec<C> orderBy(Supplier<List<SortPart>> supplier);

        BatchSingleLimitSpec<C> ifOrderBy(@Nullable SortPart sortPart);

        BatchSingleLimitSpec<C> ifOrderBy(Supplier<List<SortPart>> supplier);

        BatchSingleLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);

    }


    interface BatchSingleLimitSpec<C> extends Update.BatchParamSpec<C> {

        BatchParamSpec<C> limit(long rowCount);

        BatchParamSpec<C> limit(Function<C, Long> function);

        BatchParamSpec<C> limit(Supplier<Long> supplier);

        BatchParamSpec<C> ifLimit(Function<C, Long> function);

        BatchParamSpec<C> ifLimit(Supplier<Long> supplier);

    }

    /*################################## blow multi-table update api interface ##################################*/


    interface MultiUpdateSpec<C> {

        <T extends IDomain> MultiIndexHintCommandSpec<C> update(SingleTableMeta<T> table, String tableAlias);

        <T extends IDomain> SinglePartitionSpec<T, C> update(SingleTableMeta<T> table);

        <P extends IDomain, T extends P> MultiIndexHintCommandSpec<P, C> update(ChildDomain<P, T> table, String tableAlias);

        <P extends IDomain, T extends P> SinglePartitionSpec<P, C> update(ChildDomain<P, T> table);
    }

    interface MultiPartitionSpec<C> extends SingleAsSpec<C> {

        MultiIndexHintCommandSpec<C> partition(String partitionName);

        MultiIndexHintCommandSpec<C> partition(String partitionName1, String partitionNam2);

        MultiIndexHintCommandSpec<C> partition(List<String> partitionNameList);

        MultiIndexHintCommandSpec<C> ifPartition(Function<C, List<String>> function);

    }


    interface MultiIndexHintCommandSpec<C> extends MySQLUpdate.JoinSpec<C>, MySQLUpdate.MultiIndexHintCommandClause<C> {

    }


    interface MultiIndexHintCommandClause<C> {

        MultiIndexWordClause<C> use();

        MultiIndexWordClause<C> ignore();

        MultiIndexWordClause<C> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        MultiIndexWordClause<C> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        MultiIndexWordClause<C> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        MultiIndexWordClause<C> ifForce(Predicate<C> predicate);

    }

    interface MultiIndexWordClause<C> {

        IndexPurposeClause<C> index();

        IndexPurposeClause<C> key();

        OnSpec<C> index(List<String> indexNameList);

        OnSpec<C> key(List<String> indexNameList);

    }

    interface IndexPurposeClause<C> {

        OnSpec<C> forOrderBy(List<String> indexNameList);

        OnSpec<C> forJoin(List<String> indexNameList);
    }


    interface JoinSpec<C> extends MultiSetSpec<C> {

    }

    interface OnSpec<C> {

        JoinSpec<C> on(List<IPredicate> predicateList);

        JoinSpec<C> on(IPredicate predicate);

        JoinSpec<C> on(IPredicate predicate1, IPredicate predicate2);

        JoinSpec<C> on(Function<C, List<IPredicate>> function);

        JoinSpec<C> on(Supplier<List<IPredicate>> supplier);

        JoinSpec<C> onId();

    }


    interface MultiSetSpec<C> {

        MultiWhereSpec<C> ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, @Nullable Object value);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, Expression<?> value);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        MultiWhereSpec<C> setNull(FieldMeta<?, ?> field);

        MultiWhereSpec<C> setDefault(FieldMeta<?, ?> field);

        <F extends Number> MultiWhereSpec<C> setPlus(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setPlus(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> MultiWhereSpec<C> setMinus(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setMinus(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> MultiWhereSpec<C> setMultiply(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setMultiply(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> MultiWhereSpec<C> setDivide(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> MultiWhereSpec<C> setMod(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setMod(FieldMeta<?, F> field, Expression<F> value);

        MultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, @Nullable Object value);

        MultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        MultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        <F extends Number> MultiWhereSpec<C> ifSetPlus(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetMinus(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetMultiply(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetDivide(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetMod(FieldMeta<?, ?> field, @Nullable F value);

    }

    interface MultiWhereSpec<C> extends MultiSetSpec<C> {

        UpdateSpec where(List<IPredicate> predicates);

        UpdateSpec where(Function<C, List<IPredicate>> function);

        UpdateSpec where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<C> where(IPredicate predicate);

    }


}
