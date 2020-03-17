package io.army.criteria.impl;

import io.army.criteria.QueryAble;
import io.army.criteria.SQLContext;
import io.army.criteria.SubQuery;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.HashMap;
import java.util.Map;

final class SubQuerySelectImpl<C> extends AbstractSelectImpl<C> implements SubQuerySelect<C>{

    private final Map<String,SubQuery> subordinateSubQueries = new HashMap<>();

    private QueryAble outerQuery;

    SubQuerySelectImpl(C criteria) {
        super(criteria);
    }

    @Override
    public  SubQuery subordinateSubQuery(String subordinateSubQueryAlias) {
        return subordinateSubQueries.get(subordinateSubQueryAlias);
    }

    @Override
    public  void outerQuery(QueryAble outerQuery) {
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
         context.dml().subQuery(this,context);
    }

    @Override
    protected void doTable(TableMeta<?> table, String tableAlias) {

    }

    @Override
    protected void doSubQuery(SubQuery subQuery, String subQueryAlias) {
        subordinateSubQueries.putIfAbsent(subQueryAlias,subQuery);
    }

    @Override
    protected void doPrepare() {

    }
}
