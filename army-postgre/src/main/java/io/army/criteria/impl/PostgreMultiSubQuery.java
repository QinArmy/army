package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.inner.postgre.PostgreInnerSubQuery;
import io.army.criteria.postgre.PostgreSubQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class PostgreMultiSubQuery<C> extends AbstractPostgreMultiSelect<C>
        implements PostgreInnerSubQuery, PostgreSubQuery {

    private final CriteriaContext criteriaContext;

    private Map<String, SubQuery> subordinateSubQueries;

    private Map<String, Selection> selectionMap;

    private QueryAble outerQuery;

    PostgreMultiSubQuery(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    @Override
    public String toString() {
        return "#Postgre SubQuery";
    }

    /*################################## blow PostgreSubQuery method ##################################*/

    @Override
    public Selection selection(String derivedFieldName) {
        if (this.selectionMap == null) {
            this.selectionMap = CriteriaUtils.createSelectionMap(selectPartList());
        }
        Selection s = this.selectionMap.get(derivedFieldName);
        if (s == null) {
            throw new CriteriaException(ErrorCode.NO_SELECTION
                    , "not found Selection[%s] from SubQuery.", derivedFieldName);
        }
        return s;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.dql().subQuery(this, context);
    }


    /*################################## blow PostgreInnerSubQuery method ##################################*/

    @Override
    public void clear() {
        super.clear();
        this.subordinateSubQueries = null;
        this.selectionMap = null;
        this.outerQuery = null;
    }

    /*################################## blow package method ##################################*/

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);

        if (this.subordinateSubQueries == null) {
            this.subordinateSubQueries = new HashMap<>();
        }
        if (this.subordinateSubQueries.putIfAbsent(subQueryAlias, subQuery) != subQuery) {
            throw new CriteriaException(ErrorCode.TABLE_ALIAS_DUPLICATION, "SubQuery[%s] duplication.", subQueryAlias);
        }
    }

    @Override
    final void doAsSelect() {
        if (this.subordinateSubQueries == null) {
            this.subordinateSubQueries = Collections.emptyMap();
        } else {
            this.subordinateSubQueries = Collections.unmodifiableMap(this.subordinateSubQueries);
        }
    }

    @Override
    final void beforeAsSelect() {
        // clear context
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();
    }
}
