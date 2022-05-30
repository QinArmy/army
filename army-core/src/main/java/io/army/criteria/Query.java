package io.army.criteria;


import io.army.lang.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
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


    interface _QuerySpec<Q extends Query> extends _RowSetSpec<Q> {

    }


    /*################################## blow select clause  interfaces ##################################*/


    interface SelectClause<C, W extends SQLWords, SR> {

        SR select(SelectItem selectItem);

        SR select(SelectItem selectItem1, SelectItem selectItem2);

        SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3);

        SR select(Consumer<Consumer<SelectItem>> consumer);

        SR select(BiConsumer<C, Consumer<SelectItem>> consumer);

        SR select(@Nullable W modifier, SelectItem selectItem);

        SR select(@Nullable W modifier, SelectItem selectItem1, SelectItem selectItem2);

        SR select(@Nullable W modifier, Consumer<Consumer<SelectItem>> consumer);

        SR select(@Nullable W modifier, BiConsumer<C, Consumer<SelectItem>> consumer);

    }


    interface _GroupClause<C, GR> {

        GR groupBy(SortItem sortItem);

        GR groupBy(SortItem sortItem1, SortItem sortItem2);

        GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        <S extends SortItem> GR groupBy(Supplier<List<S>> supplier);

        <S extends SortItem> GR groupBy(Function<C, List<S>> function);

        GR groupBy(Consumer<List<SortItem>> consumer);

        <S extends SortItem> GR ifGroupBy(Supplier<List<S>> supplier);

        <S extends SortItem> GR ifGroupBy(Function<C, List<S>> function);
    }


    interface _HavingClause<C, HR> {

        HR having(IPredicate predicate);

        HR having(IPredicate predicate1, IPredicate predicate2);

        HR having(Supplier<List<IPredicate>> supplier);

        HR having(Function<C, List<IPredicate>> function);

        HR having(Consumer<List<IPredicate>> consumer);

        HR ifHaving(@Nullable IPredicate predicate);

        HR ifHaving(Supplier<List<IPredicate>> supplier);

        HR ifHaving(Function<C, List<IPredicate>> function);

    }


    interface _UnionClause<C, UR, SP> {
        UR bracket();

        UR union(Function<C, ? extends RowSet> function);

        UR union(Supplier<? extends RowSet> supplier);

        UR ifUnion(Function<C, ? extends RowSet> function);

        UR ifUnion(Supplier<? extends RowSet> supplier);

        UR unionAll(Function<C, ? extends RowSet> function);

        UR unionAll(Supplier<? extends RowSet> supplier);

        UR ifUnionAll(Function<C, ? extends RowSet> function);

        UR ifUnionAll(Supplier<? extends RowSet> supplier);

        UR unionDistinct(Function<C, ? extends RowSet> function);

        UR unionDistinct(Supplier<? extends RowSet> supplier);

        UR ifUnionDistinct(Function<C, ? extends RowSet> function);

        UR ifUnionDistinct(Supplier<? extends RowSet> supplier);

    }


    interface _QueryUnionClause<C, UR, SP> extends _UnionClause<C, UR, SP> {

        SP union();

        SP unionAll();

        SP unionDistinct();
    }

    interface _LimitClause<C, LR> {

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
