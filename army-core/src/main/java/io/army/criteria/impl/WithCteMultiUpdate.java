package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of multi-table update with WITH CLAUSE implementation.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WithCteMultiUpdate<C, SS extends SubStatement, WE, F extends DataField, SR, FT, FS, FP, JT, JS, JP, WR, WA, U extends DmlStatement.DmlUpdate>
        extends MultiUpdate<C, F, SR, FT, FS, FP, JT, JS, JP, WR, WA, U>
        implements DialectStatement._WithCteClause<C, SS, WE> {

    WithCteMultiUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
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
