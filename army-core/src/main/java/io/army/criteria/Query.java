package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.mapping.MappingType;
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


    interface _DynamicWithCteClause<B extends CteBuilderSpec, WE> {
        WE with(Consumer<B> consumer);


        WE withRecursive(Consumer<B> consumer);

        WE ifWith(Consumer<B> consumer);

        WE ifWithRecursive(Consumer<B> consumer);

    }

    interface _StaticWithCteClause<WS> {

        WS with(String name);

        WS withRecursive(String name);

    }

    interface _StaticWithCommaClause<CR> extends Item {

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
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _FromClause<FT, FS> {

        FT from(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS from(Supplier<T> supplier, String alias);

    }

    interface _FromModifierClause<FT, FS> extends _FromClause<FT, FS> {

        FT from(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS from(Query.TabularModifier modifier, Supplier<T> supplier, String alias);
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


    interface _DynamicSelectClause<SR> extends _SelectClause<SR> {

        SR select(Consumer<Consumer<SelectItem>> consumer);


    }

    interface _DynamicModifierSelectClause<W extends SelectModifier, SR> extends _DynamicSelectClause<SR> {

        SR select(W modifier, Consumer<Consumer<SelectItem>> consumer);

    }

    interface _DynamicHintModifierSelectClause<W extends SelectModifier, SR>
            extends _DynamicModifierSelectClause<W, SR> {

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer);

    }


    interface _GroupClause<GR> {

        GR groupBy(SortItem sortItem);

        GR groupBy(SortItem sortItem1, SortItem sortItem2);

        GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        GR groupBy(Consumer<Consumer<SortItem>> consumer);

        GR ifGroupBy(Consumer<Consumer<SortItem>> consumer);

    }


    interface _HavingClause<HR> {

        HR having(IPredicate predicate);

        HR having(IPredicate predicate1, IPredicate predicate2);

        HR having(Supplier<IPredicate> supplier);

        HR having(Function<Object, IPredicate> operator, Supplier<?> operand);

        HR having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey);

        HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        HR having(Consumer<Consumer<IPredicate>> consumer);

        HR ifHaving(Consumer<Consumer<IPredicate>> consumer);

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

    interface _RowSetUnionClause<S extends RowSet, UR> {

        UR union(Supplier<S> supplier);

        UR unionAll(Supplier<S> supplier);

        UR unionDistinct(Supplier<S> supplier);
    }

    interface _RowSetIntersectClause<S extends RowSet, UR> {

        UR intersect(Supplier<S> supplier);

        UR intersectAll(Supplier<S> supplier);

        UR intersectDistinct(Supplier<S> supplier);
    }

    interface _RowSetExceptClause<S extends RowSet, UR> {

        UR except(Supplier<S> supplier);

        UR exceptAll(Supplier<S> supplier);

        UR exceptDistinct(Supplier<S> supplier);
    }

    interface _RowSetMinusClause<S extends RowSet, UR> {

        UR minus(Supplier<S> supplier);

        UR minusAll(Supplier<S> supplier);

        UR minusDistinct(Supplier<S> supplier);
    }


    interface _LimitClause<LR> extends Statement._RowCountLimitClause<LR> {

        LR limit(Expression offset, Expression rowCount);

        LR limit(BiFunction<MappingType, Number, Expression> operator, long offset, long rowCount);

        <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR limit(Consumer<BiConsumer<Expression, Expression>> consumer);


        <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR ifLimit(Consumer<BiConsumer<Expression, Expression>> consumer);

    }


    interface _StaticSpaceClause<SR> {

        SR space();
    }

    interface _LeftParenClause<LR> {

        LR leftParen();
    }


}
