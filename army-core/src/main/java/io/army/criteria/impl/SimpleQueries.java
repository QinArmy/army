package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner.*;
import io.army.dialect._Constant;
import io.army.function.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of all simple SELECT query.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleQueries<Q extends Item, W extends Query.SelectModifier, SR extends Item, SD, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, LR, LO, LF, SP, UR>
        extends JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, OR, LR, LO, LF>
        implements Query._SelectDispatcher<W, SR, SD>,
        Query._StaticSelectCommaClause<SR>,
        Query._StaticSelectSpaceClause<SR>,
        Statement._QueryWhereClause<WR, WA>,
        Query._GroupByClause<GR>,
        Query._HavingClause<HR>,
        Query._AsQueryClause<Q>,
        TabularItem.DerivedTableSpec,
        Query._QueryUnionClause<SP>,
        Query._QueryIntersectClause<SP>,
        Query._QueryExceptClause<SP>,
        Query._QueryMinusClause<SP>,
        Query._RowSetUnionClause<UR>,
        Query._RowSetIntersectClause<UR>,
        Query._RowSetExceptClause<UR>,
        Query._RowSetMinusClause<UR>,
        Query,
        _Query {

    private final Function<TypeInfer, SR> asFunc;

    private final Function<_ItemExpression<SR>, _AliasExpression<SR>> expFunc;


    private List<Hint> hintList;

    private List<? extends Query.SelectModifier> modifierList;

    private List<SelectItem> selectItemList;

    private List<_TableBlock> tableBlockList;


    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;

    private Boolean prepared;


    SimpleQueries(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
        this.asFunc = this::onAsSelection;
        this.expFunc = SQLs._getIdentity();
        CriteriaContexts.setAliasEventFunction(this.context, this.asFunc);
    }

    /*-------------------below _StaticSelectClause method-------------------*/

    //below one argument method

    @Override
    public final SR select(NamedExpression exp) {
        this.context.onAddSelectItem(exp);
        return (SR) this;
    }

    @Override
    public final _AliasExpression<SR> select(Supplier<Expression> supplier) {
        return this.onSelectExpression(supplier.get());
    }

    @Override
    public final <R extends Item> R select(SqlFunction<_AliasExpression<SR>, SR, R> function) {
        return function.apply(this.expFunc, this.asFunc);
    }

    @Override
    public final SR select(NamedExpression exp1, NamedExpression exp2) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2);
        return (SR) this;
    }

    @Override
    public final <T> _AliasExpression<SR> select(Function<T, Expression> operator, Supplier<T> supplier) {
        return this.onSelectExpression(operator.apply(supplier.get()));
    }

    @Override
    public final _AliasExpression<SR> select(Function<Expression, Expression> operator, Expression exp) {
        return this.onSelectExpression(operator.apply(exp));
    }

    @Override
    public final _AliasExpression<SR> select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                             BiFunction<DataField, String, Expression> namedOperator) {
        return this.onSelectExpression(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <R extends Item> R select(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp) {
        return function.apply(exp, this.expFunc, this.asFunc);
    }

    @Override
    public final <R extends Item> R select(SqlOneFunction<_AliasExpression<SR>, SR, R> function,
                                           Supplier<Expression> supplier) {
        return function.apply(supplier.get(), this.expFunc, this.asFunc);
    }

    @Override
    public final SR select(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2)
                .onAddSelectItem(exp3);
        return (SR) this;
    }

    @Override
    public final SR select(Expression exp, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(exp, alias));
        return (SR) this;
    }

    @Override
    public final SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star) {
        this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
        return (SR) this;
    }

    @Override
    public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final SR select(Supplier<Expression> supplier, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(supplier.get(), alias));
        return (SR) this;
    }

    @Override
    public final <T> _AliasExpression<SR> select(ExpressionOperator<Expression, T, Expression> expOperator,
                                                 BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.onSelectExpression(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final <R extends Item> R select(SqlTwoFunction<_AliasExpression<SR>, SR, R> function,
                                           Expression exp1, Expression exp2) {
        return function.apply(exp1, exp2, this.expFunc, this.asFunc);
    }

    @Override
    public final SR select(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2)
                .onAddSelectItem(exp3)
                .onAddSelectItem(exp4);
        return (SR) this;
    }

    @Override
    public final <T> SR select(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(supplier.get()), alias));
        return (SR) this;
    }

    @Override
    public final SR select(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(exp), alias));
        return (SR) this;
    }

    @Override
    public final SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                           BiFunction<DataField, String, Expression> namedOperator,
                           SQLs.WordAs as, String alias) {

        this.context.onAddSelectItem(ArmySelections.forExp(fieldOperator.apply(namedOperator), alias));
        return (SR) this;
    }

    @Override
    public final _AliasExpression<SR> select(ExpressionOperator<Expression, Object, Expression> expOperator,
                                             BiFunction<Expression, Object, Expression> operator,
                                             Function<String, ?> function, String keyName) {
        return this.onSelectExpression(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final <R extends Item> R select(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                           Expression exp2, Expression exp3) {
        return function.apply(exp1, exp2, exp3, this.expFunc, this.asFunc);
    }

    @Override
    public final SR select(NamedExpression exp1, SQLs.WordAs as1, String alias1, NamedExpression exp2, SQLs.WordAs as2,
                           String alias2) {
        this.context.onAddSelectItem(ArmySelections.forExp(exp1, alias1))
                .onAddSelectItem(ArmySelections.forExp(exp2, alias2));
        return (SR) this;
    }

    @Override
    public final <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                               String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw childParentNotMatch(this.context, parent, child);
        }
        this.context.onAddSelectItem(SelectionGroups.childGroup(child, childAlias, parenAlias));
        return (SR) this;
    }

    @Override
    public final SR select(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                           Supplier<Expression> function2, SQLs.WordAs as2, String alias2) {

        this.context.onAddSelectItem(ArmySelections.forExp(function1.get(), alias1))
                .onAddSelectItem(ArmySelections.forExp(function2.get(), alias2));
        return (SR) this;
    }

    @Override
    public final SR select(ExpressionOperator<Expression, Object, Expression> expOperator,
                           BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                           String keyName, SQLs.WordAs as, String alias) {

        this.context.onAddSelectItem(ArmySelections.forExp(expOperator.apply(operator, function.apply(keyName)), alias));
        return (SR) this;
    }

    @Override
    public final _StaticSelectSpaceClause<SR> select(W modifier) {
        this.modifierList = this.asSingleModifier(modifier);
        return this;
    }

    @Override
    public final _StaticSelectSpaceClause<SR> select(List<W> modifiers) {
        this.modifierList = this.asModifierList(modifiers);
        return this;
    }

    @Override
    public final _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        return this;
    }

    /*-------------------below dynamic select clause method -------------------*/

    @Override
    public final SD selects(Consumer<Selections> consumer) {
        consumer.accept(new SelectionsImpl(this.context));
        return (SD) this;
    }

    @Override
    public final SD selects(W modifier, Consumer<Selections> consumer) {
        this.modifierList = this.asSingleModifier(modifier);
        consumer.accept(new SelectionsImpl(this.context));
        return (SD) this;
    }

    @Override
    public final SD selects(List<W> modifierList, Consumer<Selections> consumer) {
        this.modifierList = this.asModifierList(modifierList);
        consumer.accept(new SelectionsImpl(this.context));
        return (SD) this;
    }

    @Override
    public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Selections> consumer) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(new SelectionsImpl(this.context));
        return (SD) this;
    }



    /*-------------------below select space clause method -------------------*/

    @Override
    public final SR space(NamedExpression exp) {
        this.context.onAddSelectItem(exp);
        return (SR) this;
    }

    @Override
    public final _AliasExpression<SR> space(Supplier<Expression> supplier) {
        return this.onSelectExpression(supplier.get());
    }

    @Override
    public final <R extends Item> R space(SqlFunction<_AliasExpression<SR>, SR, R> function) {
        return function.apply(this.expFunc, this.asFunc);
    }

    @Override
    public final SR space(NamedExpression exp1, NamedExpression exp2) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2);
        return (SR) this;
    }

    @Override
    public final <T> _AliasExpression<SR> space(Function<T, Expression> operator, Supplier<T> supplier) {
        return this.onSelectExpression(operator.apply(supplier.get()));
    }

    @Override
    public final _AliasExpression<SR> space(Function<Expression, Expression> operator, Expression exp) {
        return this.onSelectExpression(operator.apply(exp));
    }

    @Override
    public final _AliasExpression<SR> space(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                            BiFunction<DataField, String, Expression> namedOperator) {
        return this.onSelectExpression(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <R extends Item> R space(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp) {
        return function.apply(exp, this.expFunc, this.asFunc);
    }

    @Override
    public final <R extends Item> R space(SqlOneFunction<_AliasExpression<SR>, SR, R> function,
                                          Supplier<Expression> supplier) {
        return function.apply(supplier.get(), this.expFunc, this.asFunc);
    }

    @Override
    public final SR space(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2)
                .onAddSelectItem(exp3);
        return (SR) this;
    }

    @Override
    public final SR space(Expression exp, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(exp, alias));
        return (SR) this;
    }

    @Override
    public final SR space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star) {
        this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
        return (SR) this;
    }

    @Override
    public final SR space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final SR space(Supplier<Expression> supplier, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(supplier.get(), alias));
        return (SR) this;
    }

    @Override
    public final <T> _AliasExpression<SR> space(ExpressionOperator<Expression, T, Expression> expOperator,
                                                BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.onSelectExpression(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final <R extends Item> R space(SqlTwoFunction<_AliasExpression<SR>, SR, R> function,
                                          Expression exp1, Expression exp2) {
        return function.apply(exp1, exp2, this.expFunc, this.asFunc);
    }

    @Override
    public final SR space(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2)
                .onAddSelectItem(exp3)
                .onAddSelectItem(exp4);
        return (SR) this;
    }

    @Override
    public final <T> SR space(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as,
                              String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(supplier.get()), alias));
        return (SR) this;
    }

    @Override
    public final SR space(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as,
                          String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(exp), alias));
        return (SR) this;
    }

    @Override
    public final SR space(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                          BiFunction<DataField, String, Expression> namedOperator,
                          SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(fieldOperator.apply(namedOperator), alias));
        return (SR) this;
    }

    @Override
    public final _AliasExpression<SR> space(ExpressionOperator<Expression, Object, Expression> expOperator,
                                            BiFunction<Expression, Object, Expression> operator,
                                            Function<String, ?> function, String keyName) {
        return this.onSelectExpression(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final <R extends Item> R space(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                          Expression exp2, Expression exp3) {
        return function.apply(exp1, exp2, exp3, this.expFunc, this.asFunc);
    }

    @Override
    public final SR space(NamedExpression exp1, SQLs.WordAs as1, String alias1, NamedExpression exp2,
                          SQLs.WordAs as2, String alias2) {
        this.context.onAddSelectItem(ArmySelections.forExp(exp1, alias1))
                .onAddSelectItem(ArmySelections.forExp(exp2, alias2));
        return (SR) this;
    }

    @Override
    public final <P> SR space(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                              String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw childParentNotMatch(this.context, parent, child);
        }
        this.context.onAddSelectItem(SelectionGroups.childGroup(child, childAlias, parenAlias));
        return (SR) this;
    }

    @Override
    public final SR space(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                          Supplier<Expression> function2, SQLs.WordAs as2, String alias2) {
        this.context.onAddSelectItem(ArmySelections.forExp(function1.get(), alias1))
                .onAddSelectItem(ArmySelections.forExp(function2.get(), alias2));
        return (SR) this;
    }

    @Override
    public final SR space(ExpressionOperator<Expression, Object, Expression> expOperator,
                          BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                          String keyName, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(expOperator.apply(operator, function.apply(keyName)), alias));
        return (SR) this;
    }


    /*-------------------below select comma -------------------*/

    @Override
    public final SR comma(NamedExpression exp) {
        this.context.onAddSelectItem(exp);
        return (SR) this;
    }

    @Override
    public final _AliasExpression<SR> comma(Supplier<Expression> supplier) {
        return this.onSelectExpression(supplier.get());
    }

    @Override
    public final <R extends Item> R comma(SqlFunction<_AliasExpression<SR>, SR, R> function) {
        return function.apply(this.expFunc, this.asFunc);
    }

    @Override
    public final SR comma(NamedExpression exp1, NamedExpression exp2) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2);
        return (SR) this;
    }

    @Override
    public final <T> _AliasExpression<SR> comma(Function<T, Expression> operator, Supplier<T> supplier) {
        return this.onSelectExpression(operator.apply(supplier.get()));
    }

    @Override
    public final _AliasExpression<SR> comma(Function<Expression, Expression> operator, Expression exp) {
        return this.onSelectExpression(operator.apply(exp));
    }

    @Override
    public final _AliasExpression<SR> comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                            BiFunction<DataField, String, Expression> namedOperator) {
        return this.onSelectExpression(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <R extends Item> R comma(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp) {
        return function.apply(exp, this.expFunc, this.asFunc);
    }

    @Override
    public final <R extends Item> R comma(SqlOneFunction<_AliasExpression<SR>, SR, R> function,
                                          Supplier<Expression> supplier) {
        return function.apply(supplier.get(), this.expFunc, this.asFunc);
    }

    @Override
    public final SR comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2)
                .onAddSelectItem(exp3);
        return (SR) this;
    }

    @Override
    public final SR comma(Expression exp, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(exp, alias));
        return (SR) this;
    }

    @Override
    public final SR comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star) {
        this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
        return (SR) this;
    }

    @Override
    public final SR comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
        return (SR) this;
    }

    @Override
    public final SR comma(Supplier<Expression> supplier, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(supplier.get(), alias));
        return (SR) this;
    }

    @Override
    public final <T> _AliasExpression<SR> comma(ExpressionOperator<Expression, T, Expression> expOperator,
                                                BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.onSelectExpression(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final <R extends Item> R comma(SqlTwoFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                          Expression exp2) {
        return function.apply(exp1, exp2, this.expFunc, this.asFunc);
    }

    @Override
    public final SR comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
        this.context.onAddSelectItem(exp1)
                .onAddSelectItem(exp2)
                .onAddSelectItem(exp3)
                .onAddSelectItem(exp4);
        return (SR) this;
    }

    @Override
    public final <T> SR comma(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(supplier.get()), alias));
        return (SR) this;
    }

    @Override
    public final SR comma(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(exp), alias));
        return (SR) this;
    }

    @Override
    public final SR comma(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                          BiFunction<DataField, String, Expression> namedOperator,
                          SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(fieldOperator.apply(namedOperator), alias));
        return (SR) this;
    }

    @Override
    public final _AliasExpression<SR> comma(ExpressionOperator<Expression, Object, Expression> expOperator,
                                            BiFunction<Expression, Object, Expression> operator,
                                            Function<String, ?> function, String keyName) {
        return this.onSelectExpression(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final <R extends Item> R comma(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                          Expression exp2, Expression exp3) {
        return function.apply(exp1, exp2, exp3, this.expFunc, this.asFunc);
    }

    @Override
    public final SR comma(NamedExpression exp1, SQLs.WordAs as1, String alias1, NamedExpression exp2,
                          SQLs.WordAs as2, String alias2) {
        this.context.onAddSelectItem(ArmySelections.forExp(exp1, alias1))
                .onAddSelectItem(ArmySelections.forExp(exp2, alias2));
        return (SR) this;
    }

    @Override
    public final <P> SR comma(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                              String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw childParentNotMatch(this.context, parent, child);
        }
        this.context.onAddSelectItem(SelectionGroups.childGroup(child, childAlias, parenAlias));
        return (SR) this;
    }

    @Override
    public final SR comma(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                          Supplier<Expression> function2, SQLs.WordAs as2, String alias2) {
        this.context.onAddSelectItem(ArmySelections.forExp(function1.get(), alias1))
                .onAddSelectItem(ArmySelections.forExp(function2.get(), alias2));
        return (SR) this;
    }

    @Override
    public final SR comma(ExpressionOperator<Expression, Object, Expression> expOperator,
                          BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                          String keyName, SQLs.WordAs as, String alias) {
        this.context.onAddSelectItem(ArmySelections.forExp(expOperator.apply(operator, function.apply(keyName)), alias));
        return (SR) this;
    }

    /*################################## blow FromSpec method ##################################*/


    @Override
    public final WR ifWhere(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        return (WR) this;
    }


    @Override
    public final GR groupBy(SortItem sortItem) {
        this.groupByList = Collections.singletonList((ArmySortItem) sortItem);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2) {
        this.groupByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.groupByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        return (GR) this;
    }


    @Override
    public final GR groupBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(true);
    }


    @Override
    public final GR ifGroupBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(false);
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate) {
        if (this.groupByList != null) {
            if (predicate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.havingList = Collections.singletonList((OperationPredicate<?>) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate1, final @Nullable IPredicate predicate2) {
        if (this.groupByList != null) {
            if (predicate1 == null || predicate2 == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.havingList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate<?>) predicate1
                    , (OperationPredicate<?>) predicate2
            );
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<IPredicate> supplier) {
        if (this.groupByList != null) {
            this.having(supplier.get());
        }
        return (HR) this;
    }


    @Override
    public final HR having(Function<Object, IPredicate> operator, Supplier<?> operand) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.apply(operandKey)));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand) {
        if (this.groupByList != null) {
            this.having(operator.apply(firstOperand.get(), secondOperand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(false);
        }
        return (HR) this;
    }


    @Override
    public final HR ifHaving(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(true);
        }
        return (HR) this;
    }

    @Override
    public final SP union() {
        return this.onUnion(UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.onUnion(UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.onUnion(UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.onUnion(UnionType.INTERSECT);
    }


    @Override
    public final SP intersectAll() {
        return this.onUnion(UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.onUnion(UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.onUnion(UnionType.EXCEPT);
    }


    @Override
    public final SP exceptAll() {
        return this.onUnion(UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.onUnion(UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.onUnion(UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.onUnion(UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.onUnion(UnionType.MINUS_DISTINCT);
    }

    @Override
    public final <S extends RowSet> UR union(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.UNION, supplier);
    }

    @Override
    public final <S extends RowSet> UR unionAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.UNION_ALL, supplier);
    }

    @Override
    public final <S extends RowSet> UR unionDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.UNION_DISTINCT, supplier);
    }

    @Override
    public final <S extends RowSet> UR except(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.EXCEPT, supplier);
    }

    @Override
    public final <S extends RowSet> UR exceptAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.EXCEPT_ALL, supplier);
    }

    @Override
    public final <S extends RowSet> UR exceptDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.EXCEPT_DISTINCT, supplier);
    }

    @Override
    public final <S extends RowSet> UR intersect(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.INTERSECT, supplier);
    }

    @Override
    public final <S extends RowSet> UR intersectAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.INTERSECT_ALL, supplier);
    }

    @Override
    public final <S extends RowSet> UR intersectDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.INTERSECT_DISTINCT, supplier);
    }

    @Override
    public final <S extends RowSet> UR minus(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.MINUS, supplier);
    }

    @Override
    public final <S extends RowSet> UR minusAll(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.MINUS_ALL, supplier);
    }

    @Override
    public final <S extends RowSet> UR minusDistinct(Supplier<S> supplier) {
        return this.unionRowSet(UnionType.MINUS_DISTINCT, supplier);
    }

    /*################################## blow _Query method ##################################*/

    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<? extends SQLWords> modifierList() {
        final List<? extends SQLWords> list = this.modifierList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final int selectionSize() {
        return this.context.selectionSize();
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        final List<? extends SelectItem> list = this.selectItemList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_TableBlock> tableBlockList() {
        final List<_TableBlock> list = this.tableBlockList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final List<? extends SortItem> groupByList() {
        final List<? extends SortItem> list = this.groupByList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_Predicate> havingList() {
        final List<_Predicate> list = this.havingList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public final Q asQuery() {
        this.endQueryStatement();
        return this.onAsQuery();
    }


    @Override
    public final void clear() {
        this.hintList = null;
        this.modifierList = null;
        this.selectItemList = null;
        this.tableBlockList = null;

        this.groupByList = null;
        this.havingList = null;
        this.onClear();
    }

    @Override
    public final Selection selection(final String derivedAlias) {
        if (!(this instanceof SubQuery)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.context.selection(derivedAlias);
    }

    abstract SP createQueryUnion(UnionType unionType);

    abstract void onEndQuery();

    abstract Q onAsQuery();

    abstract void onClear();

    abstract List<W> asModifierList(@Nullable List<W> modifiers);

    abstract List<Hint> asHintList(@Nullable List<Hint> hints);


    abstract boolean isErrorModifier(W modifier);


    private SP onUnion(UnionType unionType) {
        this.endQueryStatement();
        return this.createQueryUnion(unionType);
    }

    private UR unionRowSet(UnionType unionType, Supplier<? extends RowSet> supplier) {
        ContextStack.pop(this.context).endContextBeforeSelect();
        return null;
    }


    private List<W> asSingleModifier(final @Nullable W modifier) {
        if (modifier == null || this.isErrorModifier(modifier)) {
            String m = String.format("%s syntax error.", modifier);
            throw ContextStack.criteriaError(this.context, m);
        }
        return Collections.singletonList(modifier);
    }


    private SR onAsSelection(final TypeInfer selection) {
        if (!(selection instanceof ArmySelections)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.context.onAddSelectItem((Selection) selection);
        return (SR) this;
    }


    private void endQueryStatement() {
        _Assert.nonPrepared(this.prepared);
        // hint list
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        // modifier list
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        final CriteriaContext context = this.context;
        // selection list
        this.selectItemList = context.endSelectClause();

        this.endWhereClause();

        // group by and having
        if (this.groupByList == null) {
            this.groupByList = Collections.emptyList();
            this.havingList = Collections.emptyList();
        } else if (this.havingList == null) {
            this.havingList = Collections.emptyList();
        }

        this.endOrderByClause();

        this.onEndQuery();

        this.tableBlockList = context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;

    }

    private _AliasExpression<SR> onSelectExpression(final @Nullable Expression expression) {
        final _AliasExpression<SR> wrap;
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (expression instanceof NonOperationExpression) {
            String m = String.format("error expression[%s]", expression);
            throw ContextStack.criteriaError(this.context, m);
        } else if (!(expression instanceof OperationExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (((OperationExpression<?>) expression).function == this.asFunc) {
            wrap = (OperationExpression<SR>) expression;
        } else if (expression instanceof OperationPredicate) {
            wrap = Expressions.wrapPredicate((OperationPredicate<?>) expression, this.asFunc);
        } else {
            wrap = Expressions.wrapExpression((OperationExpression<?>) expression, this.asFunc);
        }
        return wrap;
    }

    private void addGroupByItem(final @Nullable SortItem sortItem) {
        if (sortItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            itemList = new ArrayList<>();
            this.groupByList = itemList;
        } else if (!(itemList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        itemList.add((ArmySortItem) sortItem);
    }

    private GR endGroupBy(final boolean required) {
        final List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            if (required) {
                throw ContextStack.criteriaError(this.context, "group by clause is empty");
            }
            //null,no-op
        } else if (itemList instanceof ArrayList) {
            this.groupByList = _CollectionUtils.unmodifiableList(itemList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (GR) this;
    }

    private void addHavingPredicate(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.havingList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.havingList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        predicateList.add((OperationPredicate<?>) predicate);
    }

    private void endHaving(final boolean optional) {
        final List<_Predicate> predicateList = this.havingList;
        if (this.groupByList == null) {
            this.havingList = Collections.emptyList();
        } else if (predicateList == null) {
            if (!optional) {
                throw ContextStack.criteriaError(this.context, "having clause is empty");
            }
            this.havingList = Collections.emptyList();
        } else if (predicateList instanceof ArrayList) {
            this.havingList = _CollectionUtils.unmodifiableList(predicateList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }


    private static CriteriaException childParentNotMatch(CriteriaContext context, ParentTableMeta<?> parent,
                                                         ChildTableMeta<?> child) {
        String m = String.format("%s isn't child of %s", child, parent);
        return ContextStack.criteriaError(context, m);
    }


    static abstract class WithCteSimpleQueries<Q extends Item, B extends CteBuilderSpec, WE, W extends Query.SelectModifier, SR extends Item, SD, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, LR, LO, LF, SP>
            extends SimpleQueries<Q, W, SR, SD, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, LR, LO, LF, SP>
            implements DialectStatement._DynamicWithClause<B, WE>
            , _Statement._WithClauseSpec
            , Query._WithSelectDispatcher<B, WE, W, SR, SD> {

        private boolean recursive;

        private List<_Cte> cteList;

        WithCteSimpleQueries(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(context);
            if (withSpec != null) {
                this.recursive = withSpec.isRecursive();
                this.cteList = withSpec.cteList();
            }
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }

        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, true);
        }

        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }

        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endWithClause(builder, false);
        }


        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            final List<_Cte> list = this.cteList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }


        abstract B createCteBuilder(boolean recursive);


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);
            return (WE) this;
        }

        private WE endWithClause(final B builder, final boolean required) {
            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }

    }//WithCteSimpleQueries


    enum LockWaitOption implements SQLWords {

        NOWAIT(" NOWAIT"),
        SKIP_LOCKED(" SKIP LOCKED");

        private final String spaceWords;

        LockWaitOption(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(LockWaitOption.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//LockWaitOption


    static abstract class SelectClauseDispatcher<W extends Query.SelectModifier, SR extends Item, SD>
            implements Query._SelectDispatcher<W, SR, SD> {

        SelectClauseDispatcher() {
        }

        /*-------------------below _StaticSelectClause method-------------------*/

        @Override
        public final SR select(NamedExpression exp) {
            return this.createSelectClause().select(exp);
        }

        @Override
        public final _AliasExpression<SR> select(Supplier<Expression> supplier) {
            return this.createSelectClause().select(supplier);
        }

        @Override
        public final <R extends Item> R select(SqlFunction<_AliasExpression<SR>, SR, R> function) {
            return this.createSelectClause().select(function);
        }

        @Override
        public final SR select(NamedExpression exp1, NamedExpression exp2) {
            return this.createSelectClause().select(exp1, exp2);
        }

        @Override
        public final <T> _AliasExpression<SR> select(Function<T, Expression> operator, Supplier<T> supplier) {
            return this.createSelectClause().select(operator, supplier);
        }

        @Override
        public final _AliasExpression<SR> select(Function<Expression, Expression> operator, Expression exp) {
            return this.createSelectClause().select(operator, exp);
        }

        @Override
        public final _AliasExpression<SR> select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                                 BiFunction<DataField, String, Expression> namedOperator) {
            return this.createSelectClause().select(fieldOperator, namedOperator);
        }

        @Override
        public final <R extends Item> R select(SqlOneFunction<_AliasExpression<SR>, SR, R> function, Expression exp) {
            return this.createSelectClause().select(function, exp);
        }

        @Override
        public final <R extends Item> R select(SqlOneFunction<_AliasExpression<SR>, SR, R> function,
                                               Supplier<Expression> supplier) {
            return this.createSelectClause().select(function, supplier);
        }

        @Override
        public final SR select(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
            return this.createSelectClause().select(exp1, exp2, exp3);
        }

        @Override
        public final SR select(Expression exp, SQLs.WordAs as, String alias) {
            return this.createSelectClause().select(exp, as, alias);
        }

        @Override
        public final SR select(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star) {
            return this.createSelectClause().select(derivedAlias, period, star);
        }

        @Override
        public final SR select(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            return this.createSelectClause().select(tableAlias, period, table);
        }

        @Override
        public final SR select(Supplier<Expression> supplier, SQLs.WordAs as, String alias) {
            return this.createSelectClause().select(supplier, as, alias);
        }

        @Override
        public final <T> _AliasExpression<SR> select(ExpressionOperator<Expression, T, Expression> expOperator,
                                                     BiFunction<Expression, T, Expression> operator,
                                                     Supplier<T> getter) {
            return this.createSelectClause().select(expOperator, operator, getter);
        }

        @Override
        public final <R extends Item> R select(SqlTwoFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                               Expression exp2) {
            return this.createSelectClause().select(function, exp1, exp2);
        }

        @Override
        public final SR select(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
            return this.createSelectClause().select(exp1, exp2, exp3, exp4);
        }

        @Override
        public final <T> SR select(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias) {
            return this.createSelectClause().select(operator, supplier, as, alias);
        }

        @Override
        public final SR select(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias) {
            return this.createSelectClause().select(operator, exp, as, alias);
        }

        @Override
        public final SR select(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                               BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias) {
            return this.createSelectClause().select(fieldOperator, namedOperator, as, alias);
        }

        @Override
        public final _AliasExpression<SR> select(ExpressionOperator<Expression, Object, Expression> expOperator,
                                                 BiFunction<Expression, Object, Expression> operator,
                                                 Function<String, ?> function, String keyName) {
            return this.createSelectClause().select(expOperator, operator, function, keyName);
        }

        @Override
        public final <R extends Item> R select(SqlThreeFunction<_AliasExpression<SR>, SR, R> function, Expression exp1,
                                               Expression exp2, Expression exp3) {
            return this.createSelectClause().select(function, exp1, exp2, exp3);
        }

        @Override
        public final SR select(NamedExpression exp1, SQLs.WordAs as1, String alias1, NamedExpression exp2,
                               SQLs.WordAs as2, String alias2) {
            return this.createSelectClause().select(exp1, as1, alias1, exp2, as2, alias2);
        }

        @Override
        public final <P> SR select(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                   String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            return this.createSelectClause().select(parenAlias, period1, parent, childAlias, period2, child);
        }

        @Override
        public final SR select(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                               Supplier<Expression> function2, SQLs.WordAs as2, String alias2) {
            return this.createSelectClause().select(function1, as1, alias1, function2, as2, alias2);
        }

        @Override
        public final SR select(ExpressionOperator<Expression, Object, Expression> expOperator,
                               BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                               String keyName, SQLs.WordAs as, String alias) {
            return this.createSelectClause().select(expOperator, operator, function, keyName, as, alias);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(W modifier) {
            return this.createSelectClause().select(modifier);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(List<W> modifiers) {
            return this.createSelectClause().select(modifiers);
        }

        @Override
        public final _StaticSelectSpaceClause<SR> select(Supplier<List<Hint>> hints, List<W> modifiers) {
            return this.createSelectClause().select(hints, modifiers);
        }

        @Override
        public final SD selects(Consumer<Selections> consumer) {
            return this.createSelectClause().selects(consumer);
        }

        @Override
        public final SD selects(W modifier, Consumer<Selections> consumer) {
            return this.createSelectClause().selects(modifier, consumer);
        }

        @Override
        public final SD selects(List<W> modifierList, Consumer<Selections> consumer) {
            return this.createSelectClause().selects(modifierList, consumer);
        }

        @Override
        public final SD selects(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Selections> consumer) {
            return this.createSelectClause().selects(hints, modifiers, consumer);
        }

        abstract Query._SelectDispatcher<W, SR, SD> createSelectClause();


    }//SelectClauseDispatcher


    static abstract class WithBuilderSelectClauseDispatcher<B extends CteBuilderSpec, WE, W extends Query.SelectModifier, SR extends Item, SD>
            extends SelectClauseDispatcher<W, SR, SD>
            implements DialectStatement._DynamicWithClause<B, WE>
            , _WithClauseSpec {

        final CriteriaContext outerContext;

        private CriteriaContext withClauseContext;

        private boolean recursive;

        private List<_Cte> cteList;


        WithBuilderSelectClauseDispatcher(@Nullable CriteriaContext outerContext) {
            this.outerContext = outerContext;
        }


        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.innerCreateCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
                this.cteList = cteList;
            }
            return cteList;
        }

        abstract B createCteBuilder(boolean recursive, CriteriaContext withClauseContext);

        abstract _SelectDispatcher<W, SR, SD> onSelectClause(@Nullable _WithClauseSpec spec);


        @Override
        final _SelectDispatcher<W, SR, SD> createSelectClause() {
            return this.onSelectClause(this.cteList == null ? null : this);
        }

        @Nullable
        final _WithClauseSpec getWithClause() {
            return this.cteList == null ? null : this;
        }

        final void resetWithClause() {
            if (this.withClauseContext != null) {
                throw new IllegalStateException("withClauseContext non-null");
            }
            this.cteList = null;
            this.recursive = false;
        }

        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            final CriteriaContext withClauseContext = this.withClauseContext;
            assert withClauseContext != null;
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(withClauseContext);
            }
            this.recursive = builder.isRecursive();
            this.cteList = withClauseContext.endWithClause(builder.isRecursive(), required);
            ContextStack.pop(withClauseContext);
            withClauseContext.endContext();
            this.withClauseContext = null;
            return (WE) this;
        }

        private B innerCreateCteBuilder(boolean recursive) {
            CriteriaContext withClauseContext = this.withClauseContext;
            if (withClauseContext != null) {
                throw ContextStack.castCriteriaApi(withClauseContext);
            }
            withClauseContext = CriteriaContexts.withClauseContext(this.outerContext);
            ContextStack.push(withClauseContext);
            this.withClauseContext = withClauseContext;
            return this.createCteBuilder(recursive, withClauseContext);
        }


    }//WithBuilderSelectClauseDispatcher


    static abstract class ComplexSelectCommand<W extends Query.SelectModifier, SR extends Item, SD, RR>
            extends SelectClauseDispatcher<W, SR, SD>
            implements Statement._LeftParenStringQuadraOptionalSpec<RR>
            , _RightParenClause<RR> {

        final CriteriaContext context;

        private Statement._LeftParenStringQuadraOptionalSpec<RR> quadraClause;

        ComplexSelectCommand(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final RR rightParen() {
            return (RR) this;
        }

        @Override
        public final _RightParenClause<RR> leftParen(String string) {
            this.columnAliasClauseEnd(Collections.singletonList(string));
            return this;
        }

        @Override
        public final _CommaStringDualSpec<RR> leftParen(String string1, String string2) {
            return this.stringQuadraClause()
                    .leftParen(string1, string2);
        }

        @Override
        public final _CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4) {
            return this.stringQuadraClause()
                    .leftParen(string1, string2, string3, string4);
        }

        @Override
        public final _RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer) {
            return this.stringQuadraClause()
                    .leftParen(consumer);
        }

        @Override
        public final _RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer) {
            return this.stringQuadraClause()
                    .leftParenIf(consumer);
        }


        abstract RR columnAliasClauseEnd(List<String> list);


        private Statement._LeftParenStringQuadraOptionalSpec<RR> stringQuadraClause() {
            Statement._LeftParenStringQuadraOptionalSpec<RR> clause = this.quadraClause;
            if (clause == null) {
                clause = CriteriaSupports.stringQuadra(this.context, this::columnAliasClauseEnd);
                this.quadraClause = clause;
            }
            return clause;
        }

    }//ComplexSelectCommand


    static final class UnionSubQuery extends UnionSubRowSet implements SubQuery {

        UnionSubQuery(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSubQuery

    static final class UnionSelect extends UnionRowSet implements Select {


        UnionSelect(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


    }//UnionSelect


    private static final class SelectionsImpl implements Selections {

        private final CriteriaContext context;

        private final Function<_ItemExpression<Selections>, _AliasExpression<Selections>> expFunc;

        private final Function<TypeInfer, Selections> asFunc;


        /**
         * @see #selects(Consumer)
         */
        private SelectionsImpl(CriteriaContext context) {
            this.context = context;
            this.expFunc = SQLs._getIdentity();
            this.asFunc = this::onAsSelection;
        }

        @Override
        public Selections selection(NamedExpression exp) {
            this.context.onAddSelectItem(exp);
            return this;
        }

        @Override
        public _AliasExpression<Selections> selection(Supplier<Expression> supplier) {
            return this.onSelectExpression(supplier.get());
        }

        @Override
        public <R extends Item> R selection(SqlFunction<_AliasExpression<Selections>, Selections, R> function) {
            return function.apply(this.expFunc, this.asFunc);
        }

        @Override
        public Selections selection(NamedExpression exp1, NamedExpression exp2) {
            this.context.onAddSelectItem(exp1)
                    .onAddSelectItem(exp2);
            return this;
        }

        @Override
        public <T> _AliasExpression<Selections> selection(Function<T, Expression> operator, Supplier<T> supplier) {
            return this.onSelectExpression(operator.apply(supplier.get()));
        }

        @Override
        public _AliasExpression<Selections> selection(Function<Expression, Expression> operator, Expression exp) {
            return this.onSelectExpression(operator.apply(exp));
        }

        @Override
        public _AliasExpression<Selections> selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                                      BiFunction<DataField, String, Expression> namedOperator) {
            return this.onSelectExpression(fieldOperator.apply(namedOperator));
        }

        @Override
        public <R extends Item> R selection(SqlOneFunction<_AliasExpression<Selections>, Selections, R> function,
                                            Expression exp) {
            return function.apply(exp, this.expFunc, this.asFunc);
        }

        @Override
        public <R extends Item> R selection(SqlOneFunction<_AliasExpression<Selections>, Selections, R> function,
                                            Supplier<Expression> supplier) {
            return function.apply(supplier.get(), this.expFunc, this.asFunc);
        }

        @Override
        public Selections selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
            this.context.onAddSelectItem(exp1)
                    .onAddSelectItem(exp2)
                    .onAddSelectItem(exp3);
            return this;
        }

        @Override
        public Selections selection(Expression exp, SQLs.WordAs as, String alias) {
            this.context.onAddSelectItem(ArmySelections.forExp(exp, alias));
            return this;
        }

        @Override
        public Selections selection(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star) {
            this.context.onAddSelectItem(SelectionGroups.derivedGroup(derivedAlias));
            return this;
        }

        @Override
        public Selections selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
            this.context.onAddSelectItem(SelectionGroups.singleGroup(table, tableAlias));
            return this;
        }

        @Override
        public Selections selection(Supplier<Expression> supplier, SQLs.WordAs as, String alias) {
            this.context.onAddSelectItem(ArmySelections.forExp(supplier.get(), alias));
            return this;
        }

        @Override
        public <T> _AliasExpression<Selections> selection(ExpressionOperator<Expression, T, Expression> expOperator,
                                                          BiFunction<Expression, T, Expression> operator,
                                                          Supplier<T> getter) {
            return this.onSelectExpression(expOperator.apply(operator, getter.get()));
        }

        @Override
        public <R extends Item> R selection(SqlTwoFunction<_AliasExpression<Selections>, Selections, R> function,
                                            Expression exp1, Expression exp2) {
            return function.apply(exp1, exp2, this.expFunc, this.asFunc);
        }

        @Override
        public Selections selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3,
                                    NamedExpression exp4) {
            this.context.onAddSelectItem(exp1)
                    .onAddSelectItem(exp2)
                    .onAddSelectItem(exp3)
                    .onAddSelectItem(exp4);
            return this;
        }

        @Override
        public <T> Selections selection(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as,
                                        String alias) {
            this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(supplier.get()), alias));
            return this;
        }

        @Override
        public Selections selection(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as,
                                    String alias) {
            this.context.onAddSelectItem(ArmySelections.forExp(operator.apply(exp), alias));
            return this;
        }

        @Override
        public Selections selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                    BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as,
                                    String alias) {
            this.context.onAddSelectItem(ArmySelections.forExp(fieldOperator.apply(namedOperator), alias));
            return this;
        }

        @Override
        public _AliasExpression<Selections> selection(ExpressionOperator<Expression, Object, Expression> expOperator,
                                                      BiFunction<Expression, Object, Expression> operator,
                                                      Function<String, ?> function, String keyName) {
            return this.onSelectExpression(expOperator.apply(operator, function.apply(keyName)));
        }

        @Override
        public <R extends Item> R selection(SqlThreeFunction<_AliasExpression<Selections>, Selections, R> function,
                                            Expression exp1, Expression exp2, Expression exp3) {
            return function.apply(exp1, exp2, exp3, this.expFunc, this.asFunc);
        }

        @Override
        public Selections selection(NamedExpression exp1, SQLs.WordAs as1, String alias1, NamedExpression exp2,
                                    SQLs.WordAs as2, String alias2) {
            this.context.onAddSelectItem(ArmySelections.forExp(exp1, alias1))
                    .onAddSelectItem(ArmySelections.forExp(exp2, alias2));
            return this;
        }

        @Override
        public <P> Selections selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                                        String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
            if (child.parentMeta() != parent) {
                throw childParentNotMatch(this.context, parent, child);
            }
            this.context.onAddSelectItem(SelectionGroups.childGroup(child, childAlias, parenAlias));
            return this;
        }

        @Override
        public Selections selection(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                                    Supplier<Expression> function2, SQLs.WordAs as2, String alias2) {
            this.context.onAddSelectItem(ArmySelections.forExp(function1.get(), alias1))
                    .onAddSelectItem(ArmySelections.forExp(function2.get(), alias2));
            return this;
        }

        @Override
        public Selections selection(ExpressionOperator<Expression, Object, Expression> expOperator,
                                    BiFunction<Expression, Object, Expression> operator,
                                    Function<String, ?> function, String keyName, SQLs.WordAs as, String alias) {
            this.context.onAddSelectItem(ArmySelections.forExp(expOperator.apply(operator, function.apply(keyName)), alias));
            return this;
        }

        private _AliasExpression<Selections> onSelectExpression(final @Nullable Expression expression) {
            final _AliasExpression<Selections> wrap;
            if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (expression instanceof NonOperationExpression) {
                String m = String.format("error expression[%s]", expression);
                throw ContextStack.criteriaError(this.context, m);
            } else if (!(expression instanceof OperationExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            } else if (((OperationExpression<?>) expression).function == this.asFunc) {
                wrap = (OperationExpression<Selections>) expression;
            } else if (expression instanceof OperationPredicate) {
                wrap = Expressions.wrapPredicate((OperationPredicate<?>) expression, this.asFunc);
            } else {
                wrap = Expressions.wrapExpression((OperationExpression<?>) expression, this.asFunc);
            }
            return wrap;
        }

        private Selections onAsSelection(final TypeInfer selection) {
            if (!(selection instanceof ArmySelections)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.context.onAddSelectItem((Selection) selection);
            return this;
        }


    }//SelectionsImpl


}
