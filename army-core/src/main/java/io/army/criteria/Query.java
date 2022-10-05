package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.List;
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

    /**
     * @see SQLs#ALL
     * @see SQLs#DISTINCT
     */
    interface SelectModifier {

    }

    /**
     * @see SQLs#ALL
     * @see SQLs#DISTINCT
     */
    interface UnionModifier {

    }

    interface SelectStar {

    }

    interface TabularModifier {

    }



    /*-------------------below clause interfaces -------------------*/


    interface _QuerySpec<Q extends Item> extends _RowSetSpec<Q> {

    }


    /**
     * <p>
     * This interface representing FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type.
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _FromClause<C, FT, FS> {

        FT from(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS from(Supplier<T> supplier, String alias);

        <T extends TabularItem> FS from(Function<C, T> function, String alias);

    }

    /**
     * <p>
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FS> same with the FS of {@link Query._FromClause}
     * @see Query._FromClause
     * @since 1.0
     */
    interface _FromCteClause<FS> {

        FS from(String cteName);

        FS from(String cteName, String alias);

    }

    interface _FromModifierClause<C, FM extends TabularModifier, FT, FS> extends _FromClause<C, FT, FS> {

        FT from(FM modifier, TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS from(FM modifier, Supplier<T> supplier, String alias);

        <T extends TabularItem> FS from(FM modifier, Function<C, T> function, String alias);
    }


    interface _FromLeftParenClause<FL> {

        FL fromLeftParen();

    }

    /**
     * <p>
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FP> next clause java type
     * @see _FromClause
     * @since 1.0
     */
    interface _DialectFromClause<FP> {

        FP from(TableMeta<?> table);
    }


    /*################################## blow select clause  interfaces ##################################*/


    interface _SelectClause<SR> {

        SR select(SelectItem selectItem);

        SR select(SelectItem selectItem1, SelectItem selectItem2);

        SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3);


    }


    interface _DynamicSelectClause<C, SR> {

        SR select(SelectStar star);

        SR select(Consumer<Consumer<SelectItem>> consumer);

        SR select(BiConsumer<C, Consumer<SelectItem>> consumer);


    }

    interface _DynamicModifierSelectClause<C, W extends SelectModifier, SR> extends _DynamicSelectClause<C, SR> {

        SR select(W modifier, SelectStar star);

        SR select(W modifier, Consumer<Consumer<SelectItem>> consumer);

        SR select(W modifier, BiConsumer<C, Consumer<SelectItem>> consumer);
    }

    interface _DynamicHintModifierSelectClause<C, W extends SelectModifier, SR>
            extends _DynamicModifierSelectClause<C, W, SR> {

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, SelectStar star);

        SR select(List<W> modifiers, SelectStar star);

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer);

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer);

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

        <S extends RowSet> UR union(Function<C, S> function);

        <S extends RowSet> UR union(Supplier<S> supplier);

        <S extends RowSet> UR union(UnionModifier modifier, Function<C, S> function);

        <S extends RowSet> UR union(UnionModifier modifier, Supplier<S> supplier);

        <S extends RowSet> UR ifUnion(Function<C, S> function);

        <S extends RowSet> UR ifUnion(Supplier<S> supplier);

        <S extends RowSet> UR ifUnion(UnionModifier modifier, Function<C, S> function);

        <S extends RowSet> UR ifUnion(UnionModifier modifier, Supplier<S> supplier);
    }


    interface _QueryUnionClause<C, UR, SP> extends _UnionClause<C, UR> {

        SP union();

        SP union(UnionModifier modifier);
    }

    interface _LimitClause<C, LR> extends Statement._RowCountLimitClause<C, LR> {

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
