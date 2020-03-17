package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.HashMap;
import java.util.Map;

final class SubQuerySelectImpl<C> extends AbstractSelectImpl<C> implements SubQuerySelect<C> {

    private final Map<String, SubQuery> subordinateSubQueries = new HashMap<>();

    private Map<String, Selection> selectionMap;

    private QueryAble outerQuery;

    SubQuerySelectImpl(C criteria) {
        super(criteria);
    }

    @Override
    public SubQuery subordinateSubQuery(String subordinateSubQueryAlias) {
        return subordinateSubQueries.get(subordinateSubQueryAlias);
    }

    @Override
    public Selection getSelection(String derivedFieldName) {
        if (this.selectionMap == null) {
            this.selectionMap = new HashMap<>();
            for (Selection selection : selectionList()) {
                if (this.selectionMap.putIfAbsent(selection.alias(), selection) != selection) {
                    throw new CriteriaException(ErrorCode.SELECTION_DUPLICATION
                            , "Selection[%s] of SubQuery duplication.", selection.alias());
                }
            }
        }
        Selection s = this.selectionMap.get(derivedFieldName);
        if (s == null) {
            throw new CriteriaException(ErrorCode.NO_SELECTION
                    , "not found Selection[%s] from SubQuery.", derivedFieldName);
        }
        return s;
    }


    @Override
    public void outerQuery(QueryAble outerQuery) {
        Assert.state(this.outerQuery == null, "outerQuery only update once.");
        this.outerQuery = outerQuery;
    }

    @Override
    public QueryAble outerQuery() {
        Assert.state(this.outerQuery != null, "outerQuery is null,SubQuery state error.");
        return this.outerQuery;
    }

    @Override
    public void appendSQL(SQLContext context) {
        context.dml().subQuery(this, context);
    }

    @Override
    protected void doTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    protected void doSubQuery(SubQuery subQuery, String subQueryAlias) {
        subordinateSubQueries.putIfAbsent(subQueryAlias, subQuery);
        CriteriaContext context = CriteriaContextHolder.getContext();
        context.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    protected void doPrepare() {

    }
}
