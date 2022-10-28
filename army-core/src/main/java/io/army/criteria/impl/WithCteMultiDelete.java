package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.DmlStatement;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._Cte;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class WithCteMultiDelete<C, SS extends SubStatement, WE, FT, FS, FP, FJ, JT, JS, JP, WR, WA, D extends DmlStatement.DmlDelete>
        extends JoinableDelete<C, FT, FS, FP, FJ, JT, JS, JP, WR, WA, D>
        implements DialectStatement._WithCteClause2<C, SS, WE> {

    WithCteMultiDelete(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final WE with(String cteName, Supplier<? extends SS> supplier) {
        CriteriaUtils.withClause(false, SQLs.cte(cteName, supplier.get()), this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE with(String cteName, Function<C, ? extends SS> function) {
        CriteriaUtils.withClause(false, SQLs.cte(cteName, function.apply(this.criteria))
                , this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE with(Consumer<Consumer<_Cte>> consumer) {
        CriteriaUtils.withClause(false, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE with(BiConsumer<C, Consumer<_Cte>> consumer) {
        CriteriaUtils.withClause(false, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWith(Consumer<Consumer<_Cte>> consumer) {
        CriteriaUtils.ifWithClause(false, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWith(BiConsumer<C, Consumer<_Cte>> consumer) {
        CriteriaUtils.ifWithClause(false, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Supplier<? extends SS> supplier) {
        CriteriaUtils.withClause(true, SQLs.cte(cteName, supplier.get()), this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Function<C, ? extends SS> function) {
        CriteriaUtils.withClause(true, SQLs.cte(cteName, function.apply(this.criteria))
                , this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(Consumer<Consumer<_Cte>> consumer) {
        CriteriaUtils.withClause(true, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE withRecursive(BiConsumer<C, Consumer<_Cte>> consumer) {
        CriteriaUtils.withClause(true, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWithRecursive(Consumer<Consumer<_Cte>> consumer) {
        CriteriaUtils.ifWithClause(true, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    @Override
    public final WE ifWithRecursive(BiConsumer<C, Consumer<_Cte>> consumer) {
        CriteriaUtils.ifWithClause(true, consumer, this.context, this::doWithCte);
        return (WE) this;
    }

    /**
     * @param cteList unmodified list
     */
    void doWithCte(boolean recursive, List<_Cte> cteList) {
        throw _Exceptions.castCriteriaApi();
    }


}
