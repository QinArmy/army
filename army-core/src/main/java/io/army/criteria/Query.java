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
    interface SelectModifier extends SQLWords {

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


    interface _DynamicWithCteClause<C, B extends CteBuilderSpec, WE> {
        WE with(Consumer<B> consumer);

        WE with(BiConsumer<C, B> consumer);

        WE withRecursive(Consumer<B> consumer);

        WE withRecursive(BiConsumer<C, B> consumer);

        WE ifWith(Consumer<B> consumer);

        WE ifWith(BiConsumer<C, B> consumer);

        WE ifWithRecursive(Consumer<B> consumer);

        WE ifWithRecursive(BiConsumer<C, B> consumer);

    }

    interface _StaticWithCteClause<WS> {

        WS with(String name);

        WS withRecursive(String name);

    }

    interface _StaticWithCommaClause<CR> {

        CR comma(String name);
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

    interface _FromModifierClause<C, FT, FS> extends _FromClause<C, FT, FS> {

        FT from(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS from(Query.TabularModifier modifier, Supplier<T> supplier, String alias);

        <T extends TabularItem> FS from(Query.TabularModifier modifier, Function<C, T> function, String alias);
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
     * @param <FC> same with the FS of {@link Query._FromClause}
     * @see Query._FromClause
     * @since 1.0
     */
    interface _FromCteClause<FC> {

        FC from(String cteName);

        FC from(String cteName, String alias);

    }

    interface _FromModifierCteClause<FC> extends _FromCteClause<FC> {

        FC from(Query.TabularModifier modifier, String cteName);

        FC from(Query.TabularModifier modifier, String cteName, String alias);
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


    interface _DynamicSelectClause<C, SR> extends _SelectClause<SR> {

        SR select(Consumer<Consumer<SelectItem>> consumer);

        SR select(BiConsumer<C, Consumer<SelectItem>> consumer);


    }

    interface _DynamicModifierSelectClause<C, W extends SelectModifier, SR> extends _DynamicSelectClause<C, SR> {

        SR select(W modifier, Consumer<Consumer<SelectItem>> consumer);

        SR select(W modifier, BiConsumer<C, Consumer<SelectItem>> consumer);
    }

    interface _DynamicHintModifierSelectClause<C, W extends SelectModifier, SR>
            extends _DynamicModifierSelectClause<C, W, SR> {

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



    interface _QueryUnionClause<SP> {

        SP union();

        SP unionAll();

        SP unionDistinct();
    }


    interface _QueryIntersectClause<SP> {

        SP intersect();

        SP intersectAll();

        SP intersectDistinct();
    }


    interface _QueryExceptClause<SP> {

        SP except();

        SP exceptAll();

        SP exceptDistinct();
    }


    interface _QueryMinusClause<SP> {

        SP minus();

        SP minusAll();

        SP minusDistinct();
    }


    interface _LimitClause<C, LR> extends Statement._RowCountLimitClause<C, LR> {

        LR limit(long offset, long rowCount);

        LR limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier);

        LR limit(Function<String, ?> function, String offsetKey, String rowCountKey);


        LR ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier);

        LR ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey);

    }


    interface _StaticSpaceClause<SR> {

        SR space();
    }

    interface _LeftParenClause<LR> {

        LR leftParen();
    }


}
