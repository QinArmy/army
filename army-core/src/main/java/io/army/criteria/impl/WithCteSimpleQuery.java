package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of the implementation {@link DialectStatement._WithCteClause}
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WithCteSimpleQuery<C, Q extends Query, WE, W extends SQLWords, SR, FT, FS, FP, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        extends SimpleQuery<C, Q, W, SR, FT, FS, FP, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        implements DialectStatement._WithCteClause<C, WE> {


    WithCteSimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final WE with(String cteName, Supplier<? extends SubQuery> supplier) {
        this.doWithCte(false, Collections.singletonList(SQLs.cte(cteName, supplier)));
        return (WE) this;
    }

    @Override
    public final WE with(String cteName, Function<C, ? extends SubQuery> function) {
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
    public final WE ifWith(Supplier<List<Cte>> supplier) {
        final List<Cte> cteList;
        cteList = supplier.get();
        if (cteList != null && cteList.size() > 0) {
            this.doWithCte(false, cteList);
        }
        return (WE) this;
    }

    @Override
    public final WE ifWith(Function<C, List<Cte>> function) {
        final List<Cte> cteList;
        cteList = function.apply(this.criteria);
        if (cteList != null && cteList.size() > 0) {
            this.doWithCte(false, cteList);
        }
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Supplier<? extends SubQuery> supplier) {
        this.doWithCte(true, Collections.singletonList(SQLs.cte(cteName, supplier)));
        return (WE) this;
    }

    @Override
    public final WE withRecursive(String cteName, Function<C, ? extends SubQuery> function) {
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

    @Override
    public final WE ifWithRecursive(Supplier<List<Cte>> supplier) {
        final List<Cte> cteList;
        cteList = supplier.get();
        if (cteList != null && cteList.size() > 0) {
            this.doWithCte(true, cteList);
        }
        return (WE) this;
    }

    @Override
    public final WE ifWithRecursive(Function<C, List<Cte>> function) {
        final List<Cte> cteList;
        cteList = function.apply(this.criteria);
        if (cteList != null && cteList.size() > 0) {
            this.doWithCte(true, cteList);
        }
        return (WE) this;
    }

    /**
     * @param cteList unmodified list
     */
    void doWithCte(boolean recursive, List<Cte> cteList) {
        throw _Exceptions.castCriteriaApi();
    }


}