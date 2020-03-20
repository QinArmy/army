package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SubQueryMultiSelect<C> extends AbstractMultiSelect<C> implements SubQuerySelect<C> {

    private Map<String, SubQuery> subordinateSubQueries;

    private Map<String, Selection> selectionMap;

    private QueryAble outerQuery;

    SubQueryMultiSelect(C criteria) {
        super(criteria);
    }

    @Override
    public SubQuery subordinateSubQuery(String subordinateSubQueryAlias) {
        return subordinateSubQueries.get(subordinateSubQueryAlias);
    }

    @Override
    public Selection selection(String derivedFieldName) {
        if (this.selectionMap == null) {
            this.selectionMap = createSelectionMap();
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
        Assert.state(this.outerQuery == null, "outerQuery only singleUpdate once.");
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
    void doClear() {
        this.subordinateSubQueries = null;
        this.selectionMap = null;
        this.outerQuery = null;
    }


    /*################################## blow package template method ##################################*/

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        if (this.subordinateSubQueries == null) {
            this.subordinateSubQueries = new HashMap<>();
        }
        this.subordinateSubQueries.putIfAbsent(subQueryAlias, subQuery);

        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    void afterDoAsSelect() {
        if (this.subordinateSubQueries != null) {
            this.subordinateSubQueries = Collections.unmodifiableMap(this.subordinateSubQueries);
        }
    }
}
