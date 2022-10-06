package io.army.criteria.impl;

import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Statement;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
abstract class OrderByClause<C, OR> implements CriteriaContextSpec, CriteriaSpec<C>
        , Statement._OrderByClause<C, OR>
        , _Statement._OrderByListSpec {

    final CriteriaContext context;

    final C criteria;

    private List<ArmySortItem> orderByList;

    OrderByClause(CriteriaContext context) {
        this.context = context;
        this.criteria = context.criteria();
    }

    @Override
    public final CriteriaContext getContext() {
        return this.context;
    }

    @Override
    public final C getCriteria() {
        return this.criteria;
    }

    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::onAddOrderBy);
        if (this.orderByList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::onAddOrderBy);
        if (this.orderByList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::onAddOrderBy);
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::onAddOrderBy);
        return (OR) this;
    }


    @Override
    public final List<? extends SortItem> orderByList() {
        final List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null || orderByList instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return orderByList;
    }

    final void endOrderByClause() {
        final List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            this.orderByList = Collections.emptyList();
        } else if (orderByList instanceof ArrayList) {
            this.orderByList = _CollectionUtils.unmodifiableList(orderByList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }
    }


    private void onAddOrderBy(final SortItem sortItem) {
        List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            orderByList = new ArrayList<>();
            this.orderByList = orderByList;
        } else if (!(orderByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        orderByList.add((ArmySortItem) sortItem);
    }


}