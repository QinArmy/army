package io.army.criteria;


import io.army.lang.Nullable;

import java.util.function.*;

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

        GR groupBy(Consumer<Consumer<SortItem>> consumer);

        GR groupBy(BiConsumer<C, Consumer<SortItem>> consumer);
        GR ifGroupBy(Consumer<Consumer<SortItem>> consumer);

        GR ifGroupBy(BiConsumer<C, Consumer<SortItem>> consumer);

    }


    interface _HavingClause<C, HR> {

        HR having(IPredicate predicate);

        HR having(IPredicate predicate1, IPredicate predicate2);

        HR having(Supplier<IPredicate> supplier);

        HR having(Function<C, IPredicate> function);

        HR having(Function<Object, IPredicate> operator, Supplier<?> operand);

        HR having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey);

        HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        HR having(Consumer<Consumer<IPredicate>> consumer);

        HR having(BiConsumer<C, Consumer<IPredicate>> consumer);

        HR ifHaving(Consumer<Consumer<IPredicate>> consumer);

        HR ifHaving(BiConsumer<C, Consumer<IPredicate>> consumer);

    }

    interface _BracketClause<UR> {

        UR bracket();
    }


    interface _UnionClause<C, UR> extends _BracketClause<UR> {

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


    interface _QueryUnionClause<C, UR, SP> extends _UnionClause<C, UR> {

        SP union();

        SP unionAll();

        SP unionDistinct();
    }

    interface _LimitClause<C, LR> extends _RowCountLimitClause<C, LR> {

        LR limit(long offset, long rowCount);

        LR limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier);

        LR limit(Function<String, ?> function, String offsetKey, String rowCountKey);

        LR limit(Consumer<BiConsumer<Long, Long>> consumer);

        LR limit(BiConsumer<C, BiConsumer<Long, Long>> consumer);

        LR ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier);

        LR ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey);

        LR ifLimit(Consumer<BiConsumer<Long, Long>> consumer);

        LR ifLimit(BiConsumer<C, BiConsumer<Long, Long>> consumer);


    }


}
