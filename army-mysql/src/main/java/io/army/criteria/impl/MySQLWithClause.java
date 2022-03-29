package io.army.criteria.impl;

import io.army.criteria.Cte;
import io.army.criteria.SubQuery;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLWithClause<C, WE> implements MySQLQuery.WithClause<C, WE> {

    final C criteria;


    boolean recursive;

    List<Cte> cteList;

    MySQLWithClause(@Nullable C criteria) {
        this.criteria = criteria;

    }


    @Override
    public final WE with(String cteName, Supplier<SubQuery> supplier) {
        this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
        return this.afterWithClause();
    }

    @Override
    public final WE with(String cteName, Function<C, SubQuery> function) {
        this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
        return this.afterWithClause();
    }

    @Override
    public final WE with(Supplier<List<Cte>> supplier) {
        this.cteList = _CollectionUtils.asUnmodifiableList(supplier.get());
        return this.afterWithClause();
    }

    @Override
    public final WE with(Function<C, List<Cte>> function) {
        this.cteList = _CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
        return this.afterWithClause();
    }

    @Override
    public final WE withRecursive(String cteName, Supplier<SubQuery> supplier) {
        this.recursive = true;
        return this.with(cteName, supplier);
    }

    @Override
    public final WE withRecursive(String cteName, Function<C, SubQuery> function) {
        this.recursive = true;
        return this.with(cteName, function);
    }

    @Override
    public final WE withRecursive(Supplier<List<Cte>> supplier) {
        this.recursive = true;
        return this.with(supplier);
    }

    @Override
    public final WE withRecursive(Function<C, List<Cte>> function) {
        this.recursive = true;
        return this.with(function);
    }

    abstract WE afterWithClause();


}
