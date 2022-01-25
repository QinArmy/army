package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Update extends Statement {


    interface UpdateSpec {

        Update asUpdate();
    }

    interface StandardUpdateClause<UR> {

        UR update(TableMeta<?> table, String tableAlias);
    }


    interface StandardUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardSetSpec<C>> {


    }

    interface StandardSetSpec<C> extends SimpleSetClause<C, StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends StandardSetSpec<C>
            , Statement.WhereClause<C, Update.UpdateSpec, StandardWhereAndSpec<C>> {


    }

    interface StandardWhereAndSpec<C> extends Statement.WhereAndClause<C, StandardWhereAndSpec<C>>, Update.UpdateSpec {

    }


    interface SetClause<C, SR> {

        /**
         * @param pairList non-null and non-empty.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(List<ItemPair> pairList);

        /**
         * @param supplier supply  non-null and non-empty list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(Supplier<List<ItemPair>> supplier);

        /**
         * @param function supply  non-null and non-empty list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(Function<C, List<ItemPair>> function);

        /**
         * @param consumer supply  non-null and non-empty list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(Consumer<List<ItemPair>> consumer);

        /**
         * @param pairList non-null list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR ifSetPairs(List<ItemPair> pairList);

        /**
         * @param supplier supply non-null list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR ifSetPairs(Supplier<List<ItemPair>> supplier);

        /**
         * @param function supply non-null list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR ifSetPairs(Function<C, List<ItemPair>> function);

        SR setExp(FieldMeta<?, ?> field, Function<C, Expression> function);

        SR setExp(FieldMeta<?, ?> field, Supplier<Expression> supplier);

        SR ifSetExp(FieldMeta<?, ?> field, Function<C, Expression> function);

    }


    interface SimpleSetClause<C, SR> extends SetClause<C, SR> {

        SR set(FieldMeta<?, ?> field, @Nullable Object paramOrExp);

        SR setLiteral(FieldMeta<?, ?> field, @Nullable Object paramOrExp);

        /**
         * <p>
         * output follow below rule:
         *    <ul>
         *        <li>parameter,equivalence : this.setExp(field, field.plus(parameter)),output : column = column + ?</li>
         *        <li>{@link Expression},equivalence : this.setExp(field, field.plus(exp)),output : column = column + exp</li>
         *    </ul>
         * </p>
         *
         * @param paramOrExp non-null parameter or {@link Expression}.
         */
        SR setPlus(FieldMeta<?, ?> field, Object paramOrExp);

        /**
         * <p>
         * output follow below rule:
         *    <ul>
         *        <li>parameter,equivalence : this.setExp(field, field.plusLiteral(parameter)),output : column = column + literal</li>
         *        <li>{@link Expression},equivalence : this.setExp(field, field.plus(exp)),output : column = column + exp</li>
         *    </ul>
         * </p>
         *
         * @param paramOrExp non-null parameter or {@link Expression}.
         */
        SR setPlusLiteral(FieldMeta<?, ?> field, Object paramOrExp);

        SR setMinus(FieldMeta<?, ?> field, Object paramOrExp);

        SR setMinusLiteral(FieldMeta<?, ?> field, Object paramOrExp);

        SR setMultiply(FieldMeta<?, ?> field, Object paramOrExp);

        SR setMultiplyLiteral(FieldMeta<?, ?> field, Object paramOrExp);

        SR setDivide(FieldMeta<?, ?> field, Object paramOrExp);

        SR setDivideLiteral(FieldMeta<?, ?> field, Object paramOrExp);

        SR setMod(FieldMeta<?, ?> field, Object paramOrExp);

        SR setModLiteral(FieldMeta<?, ?> field, Object paramOrExp);

        SR ifSet(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSet(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetLiteral(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetLiteral(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetPlus(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetPlus(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetMinus(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetMinus(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetMultiply(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetMultiply(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetDivide(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetDivide(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetMod(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetMod(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetPlusLiteral(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetPlusLiteral(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetMinusLiteral(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetMinusLiteral(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetMultiplyLiteral(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetMultiplyLiteral(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetDivideLiteral(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetDivideLiteral(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

        SR ifSetModLiteral(FieldMeta<?, ?> field, Function<String, Object> function, String keyName);

        SR ifSetModLiteral(FieldMeta<?, ?> field, Supplier<Object> paramOrExp);

    }


    interface BatchSetClause<C, SR> extends SetClause<C, SR> {

        SR setExp(FieldMeta<?, ?> field, Expression value);

        SR ifSetExp(FieldMeta<?, ?> field, Supplier<Expression> supplier);

        SR setNullable(List<FieldMeta<?, ?>> fieldList);

        SR set(List<FieldMeta<?, ?>> fieldList);

        SR set(Consumer<List<FieldMeta<?, ?>>> consumer);

        SR setNullable(Consumer<List<FieldMeta<?, ?>>> consumer);

        SR set(Function<C, List<FieldMeta<?, ?>>> function);

        SR setNullable(Function<C, List<FieldMeta<?, ?>>> function);

        SR set(Supplier<List<FieldMeta<?, ?>>> supplier);

        SR setNullable(Supplier<List<FieldMeta<?, ?>>> supplier);

        /**
         * @see SQLs#nullableNamedParam(GenericField)
         */
        SR setNullable(FieldMeta<?, ?> field);

        SR set(FieldMeta<?, ?> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        SR setPlus(FieldMeta<?, ?> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        SR setMinus(FieldMeta<?, ?> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        SR setMultiply(FieldMeta<?, ?> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        SR setDivide(FieldMeta<?, ?> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        SR setMod(FieldMeta<?, ?> field);

        SR ifSet(Function<C, List<FieldMeta<?, ?>>> function);

        SR ifSetNullable(Function<C, List<FieldMeta<?, ?>>> function);

        SR ifSet(Predicate<C> test, FieldMeta<?, ?> field);

        SR ifSetNullable(Predicate<C> test, FieldMeta<?, ?> field);

    }



    /*################################## blow batch update interface ##################################*/

    interface StandardBatchUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardBatchSetSpec<C>> {

    }


    interface StandardBatchWhereAndSpec<C> extends Statement.WhereAndClause<C, Update.StandardBatchWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Update.UpdateSpec> {

    }

    interface StandardBatchSetSpec<C> extends Update.BatchSetClause<C, StandardBatchWhereSpec<C>> {


    }

    interface StandardBatchWhereSpec<C> extends StandardBatchSetSpec<C>
            , Statement.WhereClause<C, Statement.BatchParamClause<C, Update.UpdateSpec>, Update.StandardBatchWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Update.UpdateSpec> {

    }





    /*################################## blow batch multi-table update api  ##################################*/


}
