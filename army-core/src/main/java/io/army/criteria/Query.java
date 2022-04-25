package io.army.criteria;


import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing query,is base interface of below:
 *     <ul>
 *         <li>{@link Select}</li>
 *         <li>{@link SubQuery}</li>
 *         <li>{@link ScalarSubQuery}</li>
 *     </ul>
 * </p>
 *
 * @see Select
 * @see SubQuery
 * @see ScalarSubQuery
 * @since 1.0
 */
public interface Query extends RowSet {


    interface QuerySpec<Q extends Query> {

        Q asQuery();
    }


    /*################################## blow select clause  interfaces ##################################*/


    interface SelectClause<C, SR> {

        <S extends SelectItem> SR select(List<Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectItem> SR select(List<Hint> hints, List<SQLModifier> modifiers, List<S> selectPartList);

        <S extends SelectItem> SR select(List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectItem> SR select(List<SQLModifier> modifiers, Supplier<List<S>> supplier);

        <S extends SelectItem> SR select(Function<C, List<S>> function);

        <S extends SelectItem> SR select(Supplier<List<S>> supplier);

        <S extends SelectItem> SR select(Consumer<List<S>> consumer);

        SR select(SQLModifier modifier, SelectItem selectItem);

        SR select(SelectItem selectItem);

        SR select(SelectItem selectItem1, SelectItem selectItem2);

        <S extends SelectItem> SR select(List<SQLModifier> modifiers, List<S> selectPartList);

        <S extends SelectItem> SR select(List<S> selectPartList);

        <S extends SelectItem> SR select(SQLModifier modifier, List<S> selectPartList);

        <S extends SelectItem> SR select(SQLModifier modifier, Consumer<List<S>> consumer);

    }


    interface GroupClause<C, GR> {

        GR groupBy(SortItem sortItem);

        GR groupBy(SortItem sortItem1, SortItem sortItem2);

        GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        GR groupBy(List<SortItem> sortItemList);

        GR groupBy(Function<C, List<SortItem>> function);

        GR groupBy(Supplier<List<SortItem>> supplier);

        GR groupBy(Consumer<List<SortItem>> consumer);

        GR ifGroupBy(@Nullable SortItem sortItem);

        GR ifGroupBy(Supplier<List<SortItem>> supplier);

        GR ifGroupBy(Function<C, List<SortItem>> function);
    }


    interface HavingClause<C, HR> {

        HR having(IPredicate predicate);

        HR having(IPredicate predicate1, IPredicate predicate2);

        HR having(List<IPredicate> predicateList);

        HR having(Supplier<List<IPredicate>> supplier);

        HR having(Function<C, List<IPredicate>> function);

        HR ifHaving(@Nullable IPredicate predicate);

        HR ifHaving(Supplier<List<IPredicate>> supplier);

        HR ifHaving(Function<C, List<IPredicate>> function);

    }


    interface UnionClause<C, UR, SP> {
        UR bracket();

        UR union(Function<C, ? extends RowSet> function);

        UR union(Supplier<? extends RowSet> supplier);

        UR unionAll(Function<C, ? extends RowSet> function);

        UR unionDistinct(Function<C, ? extends RowSet> function);

        UR unionAll(Supplier<? extends RowSet> supplier);

        UR unionDistinct(Supplier<? extends RowSet> supplier);
    }


    interface QueryUnionClause<C, UR, SP> extends UnionClause<C, UR, SP> {

        SP union();

        SP unionAll();

        SP unionDistinct();
    }

    interface OrderByClause<C, OR> {

        OR orderBy(SortItem sortItem);

        OR orderBy(SortItem sortItem1, SortItem sortItem2);

        OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        OR orderBy(List<SortItem> sortItemList);

        OR orderBy(Function<C, List<SortItem>> function);

        OR orderBy(Supplier<List<SortItem>> supplier);

        OR ifOrderBy(@Nullable SortItem sortItem);

        OR ifOrderBy(Supplier<List<SortItem>> supplier);

        OR ifOrderBy(Function<C, List<SortItem>> function);

    }

    interface LimitClause<C, LR> {

        LR limit(long rowCount);

        LR limit(long offset, long rowCount);

        LR limit(Supplier<? extends Number> rowCountSupplier);

        LR limit(Function<String, ?> function, String rowCountKey);

        LR limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier);

        LR limit(Function<String, ?> function, String offsetKey, String rowCountKey);

        LR limit(Function<C, LimitOption> function);

        LR ifLimit(Function<C, LimitOption> function);

        LR ifLimit(Supplier<? extends Number> rowCountSupplier);

        LR ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier);

        LR ifLimit(Function<String, ?> function, String rowCountKey);

        LR ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey);

    }


}
