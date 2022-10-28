package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Statement;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
abstract class OrderByClause<OR> extends CriteriaSupports.StatementMockSupport
        implements CriteriaContextSpec
        , Statement._StaticOrderByClause<OR>
        , Statement._StaticOrderByNullsCommaClause<OR>
        , _Statement._OrderByListSpec
        , Statement.StatementMockSpec {

    final CriteriaContext context;

    private List<ArmySortItem> orderByList;

    OrderByClause(CriteriaContext context) {
        super(context);
        this.context = context;
    }

    @Override
    public final CriteriaContext getContext() {
        return this.context;
    }

    @Override
    public final OR orderBy(Expression exp) {
        this.onAddOrderBy(exp);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp, Statement.AscDesc ascDesc) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc));
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Expression exp2) {
        this.onAddOrderBy(exp1)
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(exp1)
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp) {
        this.onAddOrderBy(exp);
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp, Statement.AscDesc ascDesc) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Expression exp2) {
        this.onAddOrderBy(exp1)
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(exp1)
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp, Statement.NullsFirstLast nullOption) {
        this.onAddOrderBy(SortItems.create(exp, nullOption));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp, Statement.AscDesc ascDesc, Statement.NullsFirstLast nullOption) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc, nullOption));
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


    final void clearOrderByList() {
        this.orderByList = null;
    }


    private List<ArmySortItem> onAddOrderBy(final SortItem sortItem) {
        List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            orderByList = new ArrayList<>();
            this.orderByList = orderByList;
        } else if (!(orderByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        orderByList.add((ArmySortItem) sortItem);
        return orderByList;
    }

    interface OrderByEventListener {

        void onOrderByEvent();

    }


}
