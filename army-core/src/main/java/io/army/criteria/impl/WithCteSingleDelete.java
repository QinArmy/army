package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Cte;
import io.army.criteria.DialectStatement;
import io.army.criteria.SubQuery;
import io.army.util._CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class WithCteSingleDelete<C, WE, WR, WA> extends SingleDelete<C, WR, WA>
        implements DialectStatement.WithCteClause<C, WE> {

    WithCteSingleDelete(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final WE with(String cteName, Supplier<SubQuery> supplier) {
        this.doWithCte(false, Collections.singletonList(SQLs.cte(cteName, supplier)));
        return (WE) this;
    }

    @Override
    public final WE with(String cteName, Function<C, SubQuery> function) {
        this.doWithCte(false, Collections.singletonList(SQLs.cte(cteName, function)));
        return (WE) this;
    }

    @Override
    public final WE with(Supplier<List<Cte>> supplier) {
        this.doWithCte(false, _CollectionUtils.asUnmodifiableList(supplier.get()));
        return (WE) this;
    }

    @Override
    public final WE with(Function<C, List<Cte>> function) {
        this.doWithCte(false, _CollectionUtils.asUnmodifiableList(function.apply(this.criteria)));
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Supplier<SubQuery> supplier) {
        this.doWithCte(true, Collections.singletonList(SQLs.cte(cteName, supplier)));
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Function<C, SubQuery> function) {
        this.doWithCte(true, Collections.singletonList(SQLs.cte(cteName, function)));
        return (WE) this;
    }

    @Override
    public final WE withRecursive(Supplier<List<Cte>> supplier) {
        this.doWithCte(true, _CollectionUtils.asUnmodifiableList(supplier.get()));
        return (WE) this;
    }

    @Override
    public final WE withRecursive(Function<C, List<Cte>> function) {
        this.doWithCte(true, _CollectionUtils.asUnmodifiableList(function.apply(this.criteria)));
        return (WE) this;
    }


    /**
     * @param cteList unmodified list
     */
    void doWithCte(boolean recursive, List<Cte> cteList) {
        String m = String.format("%s don't support with clause.", this.getClass().getName());
        throw new CriteriaException(m);
    }


}
