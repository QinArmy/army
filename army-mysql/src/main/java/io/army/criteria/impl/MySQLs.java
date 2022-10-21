package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.*;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Objects;

public abstract class MySQLs extends MySQLFuncSyntax2 {

    /**
     * private constructor
     */
    private MySQLs() {
    }

    public static MySQLInsert._PrimaryOptionSpec singleInsert() {
        return MySQLInserts.primaryInsert();
    }

    public static MySQLReplace._PrimaryOptionSpec singleReplace() {
        return MySQLReplaces.primaryReplace();
    }

    public static MySQLQuery._WithCteSpec<Select> query() {
        return MySQLQueries.primaryQuery();
    }

    public static MySQLQuery._ParenQueryClause<Select> parenQuery() {
        return MySQLQueries.primaryParenQuery();
    }

    public static MySQLQuery._WithCteSpec<SubQuery> subQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }

    public static MySQLQuery._ParenQueryClause<SubQuery> parenSubQuery() {
        return MySQLQueries.parenSubQuery(ContextStack.peek(), SQLs::_identity);
    }


    public static MySQLQuery._WithCteSpec<Expression> scalarSubQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), ScalarExpression::from);
    }

    public static MySQLQuery._ParenQueryClause<Expression> parenScalarSubQuery() {
        return MySQLQueries.parenSubQuery(ContextStack.peek(), ScalarExpression::from);
    }


    public static MySQLDqlValues._ValuesStmtValuesClause<Void, Values> valuesStmt() {
        return MySQLSimpleValues.primaryValues(null);
    }

    public static <C> MySQLDqlValues._ValuesStmtValuesClause<C, Values> valuesStmt(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSimpleValues.primaryValues(criteria);
    }

    public static MySQLDqlValues._ValuesStmtValuesClause<Void, SubValues> subValues() {
        return MySQLSimpleValues.subValues(null);
    }

    public static <C> MySQLDqlValues._ValuesStmtValuesClause<C, SubValues> subValues(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSimpleValues.subValues(criteria);
    }


    public static MySQLUpdate._SingleWithSpec singleUpdate() {
        return MySQLSingleUpdate.simple();
    }


    public static MySQLUpdate._BatchSingleWithSpec batchSingleUpdate() {
        return MySQLSingleUpdate.batch();
    }

    public static MySQLUpdate._MultiWithSpec multiUpdate() {
        return MySQLMultiUpdate.simple();
    }


    public static MySQLUpdate._BatchMultiWithSpec batchMultiUpdate() {
        return MySQLMultiUpdate.batch();
    }

    public static MySQLDelete._SingleWithSpec<Delete> singleDelete() {
        return MySQLSingleDelete.simple(SQLs::_identity);
    }


    public static MySQLDelete._BatchSingleWithSpec<Delete> batchSingleDelete() {
        return MySQLSingleDelete.batch(SQLs::_identity);
    }

    public static MySQLDelete._MultiWithSpec<Delete> multiDelete() {
        return MySQLMultiDelete.simple();
    }


    public static MySQLDelete._BatchMultiWithSpec<Delete> batchMultiDelete() {
        return MySQLMultiDelete.batch();
    }


    public static MySQLLoad._LoadDataClause<Void> loadDataStmt() {
        return MySQLLoads.loadDataStmt(null);
    }

    public static <C> MySQLLoad._LoadDataClause<C> loadDataStmt(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLLoads.loadDataStmt(criteria);
    }

    /**
     * <p>
     * create named {@link Window}.
     * </p>
     */
    public static Window._SimpleAsClause<Void, Window> window(final String windowName) {
        if (!_StringUtils.hasText(windowName)) {
            throw _Exceptions.namedWindowNoText();
        }
        final CriteriaContext criteriaContext;
        criteriaContext = ContextStack.peek();
        if (criteriaContext.criteria() != null) {
            String m = String.format("Current criteria object don't match,please use %s.%s"
                    , SQLs.class.getName(), "window(C criteria,String windowName) method.");
            throw new CriteriaException(m);
        }
        return WindowClause.standard(windowName, criteriaContext);
    }

    /**
     * <p>
     * create named {@link Window}.
     * </p>
     *
     * @param criteria non-null criteria for dynamic named window.
     */
    public static <C> Window._SimpleAsClause<C, Window> window(C criteria, final String windowName) {
        Objects.requireNonNull(criteria);
        final CriteriaContext context;
        context = ContextStack.peek();
        if (criteria != context.criteria()) {
            throw CriteriaUtils.criteriaNotMatch(context);
        }
        return WindowClause.standard(windowName, context);
    }

    public static MySQLQuery._MySQLNestedLeftParenClause<Void> nestedItems() {
        return MySQLNestedItems.create(null);
    }

    public static <C> MySQLQuery._MySQLNestedLeftParenClause<C> nestedItems(C criteria) {
        ContextStack.assertNonNull(criteria);
        return MySQLNestedItems.create(criteria);
    }

    public static MySQLQuery._IfPartitionAsClause<Void> block(TableMeta<?> table) {
        return MySQLSupports.block(null, table);
    }

    public static <C> MySQLQuery._IfPartitionAsClause<C> block(C criteria, TableMeta<?> table) {
        ContextStack.assertNonNull(criteria);
        return MySQLSupports.block(criteria, table);
    }

    public static MySQLQuery._IfUseIndexOnSpec<Void> block(TabularItem tableItem, String alias) {
        return MySQLSupports.block(null, null, tableItem, alias);
    }

    public static <C> MySQLQuery._IfUseIndexOnSpec<C> block(C criteria, TabularItem tableItem, String alias) {
        ContextStack.assertNonNull(criteria);
        return MySQLSupports.block(criteria, null, tableItem, alias);
    }

    public static MySQLQuery._IfUseIndexOnSpec<Void> lateralBlock(SubQuery subQuery, String alias) {
        return MySQLSupports.block(null, ItemWord.LATERAL, subQuery, alias);
    }

    public static <C> MySQLQuery._IfUseIndexOnSpec<C> lateralBlock(C criteria, SubQuery subQuery, String alias) {
        ContextStack.assertNonNull(criteria);
        return MySQLSupports.block(criteria, ItemWord.LATERAL, subQuery, alias);
    }


}
