package io.army.criteria.impl;

import io.army.criteria.Cte;
import io.army.criteria.DialectStatement;
import io.army.criteria.SubStatement;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WithCteValueInsert<C, T extends IDomain, SS extends SubStatement, WE, IR, VR>
        extends ValueInsert<C, T, IR, VR>
        implements DialectStatement._WithCteClause<C, SS, WE> {


    WithCteValueInsert(ValueInsetOptionClause<C, ?, ?> optionClause, TableMeta<T> table) {
        super(optionClause, table);
    }


    @Override
    public final WE with(String cteName, Supplier<? extends SS> supplier) {
        CriteriaUtils.withClause(false, SQLs.cte(cteName, supplier.get()), this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE with(String cteName, Function<C, ? extends SS> function) {
        CriteriaUtils.withClause(false, SQLs.cte(cteName, function.apply(this.criteria))
                , this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE with(Consumer<Consumer<Cte>> consumer) {
        CriteriaUtils.withClause(false, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE with(BiConsumer<C, Consumer<Cte>> consumer) {
        CriteriaUtils.withClause(false, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWith(Consumer<Consumer<Cte>> consumer) {
        CriteriaUtils.ifWithClause(false, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWith(BiConsumer<C, Consumer<Cte>> consumer) {
        CriteriaUtils.ifWithClause(false, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Supplier<? extends SS> supplier) {
        CriteriaUtils.withClause(true, SQLs.cte(cteName, supplier.get()), this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Function<C, ? extends SS> function) {
        CriteriaUtils.withClause(true, SQLs.cte(cteName, function.apply(this.criteria))
                , this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(Consumer<Consumer<Cte>> consumer) {
        CriteriaUtils.withClause(true, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(BiConsumer<C, Consumer<Cte>> consumer) {
        CriteriaUtils.withClause(true, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWithRecursive(Consumer<Consumer<Cte>> consumer) {
        CriteriaUtils.ifWithClause(true, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWithRecursive(BiConsumer<C, Consumer<Cte>> consumer) {
        CriteriaUtils.ifWithClause(true, consumer, this.criteriaContext, this::doWithCte);
        return (WE) this;
    }

    /**
     * @param cteList unmodified list
     */
    void doWithCte(boolean recursive, List<Cte> cteList) {
        throw _Exceptions.castCriteriaApi();
    }

}
