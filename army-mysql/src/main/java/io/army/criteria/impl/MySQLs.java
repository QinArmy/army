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

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static MySQLInsert._OptionSpec<Void> domainInsert() {
        return MySQLInserts.domainInsert(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static <C> MySQLInsert._OptionSpec<C> domainInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLInserts.domainInsert(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static MySQLInsert._ValueOptionSpec<Void> valueInsert() {
        return MySQLInserts.valueInsert(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static <C> MySQLInsert._ValueOptionSpec<C> valueInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLInserts.valueInsert(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static MySQLInsert._AssignmentOptionSpec<Void> assignmentInsert() {
        return MySQLInserts.assignmentInsert(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static <C> MySQLInsert._AssignmentOptionSpec<C> assignmentInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLInserts.assignmentInsert(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static MySQLInsert._QueryInsertIntoSpec<Void> queryInsert() {
        return MySQLInserts.queryInsert(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static <C> MySQLInsert._QueryInsertIntoSpec<C> queryInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLInserts.queryInsert(criteria);
    }

    public static MySQLReplace._DomainOptionSpec<Void> domainReplace() {
        return MySQLReplaces.domainReplace(null);
    }

    public static <C> MySQLReplace._DomainOptionSpec<C> domainReplace(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLReplaces.domainReplace(criteria);
    }

    public static MySQLReplace._ValueReplaceOptionSpec<Void> valueReplace() {
        return MySQLReplaces.valueReplace(null);
    }

    public static <C> MySQLReplace._ValueReplaceOptionSpec<C> valueReplace(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLReplaces.valueReplace(criteria);
    }

    public static MySQLReplace._AssignmentOptionSpec<Void> assignmentReplace() {
        return MySQLReplaces.assignmentReplace(null);
    }

    public static <C> MySQLReplace._AssignmentOptionSpec<C> assignmentReplace(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLReplaces.assignmentReplace(criteria);
    }

    public static MySQLReplace._QueryReplaceIntoSpec<Void> queryReplace() {
        return MySQLReplaces.queryReplace(null);
    }

    public static <C> MySQLReplace._QueryReplaceIntoSpec<C> queryReplace(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLReplaces.queryReplace(criteria);
    }


    public static MySQL80Query._WithSpec<Void, Select> query() {
        return MySQL80SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL80Query._WithSpec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.simpleSelect(criteria);
    }

    public static MySQL80Query._WithSpec<Void, SubQuery> subQuery() {
        return MySQL80SimpleQuery.subQuery(null);
    }

    public static <C> MySQL80Query._WithSpec<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(criteria);
    }

    public static MySQL80Query._WithSpec<Void, ScalarExpression> scalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(null);
    }

    public static <C> MySQL80Query._WithSpec<C, ScalarExpression> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(criteria);
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


    public static MySQLUpdate._SingleWithAndUpdateSpec<Void> singleUpdate() {
        return MySQLSingleUpdate.simple(null);
    }

    public static <C> MySQLUpdate._SingleWithAndUpdateSpec<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.simple(criteria);
    }

    public static MySQLUpdate._BatchSingleWithAndUpdateSpec<Void> batchSingleUpdate() {
        return MySQLSingleUpdate.batch(null);
    }

    public static <C> MySQLUpdate._BatchSingleWithAndUpdateSpec<C> batchSingleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.batch(criteria);
    }


    public static MySQLUpdate._WithAndMultiUpdateSpec<Void> multiUpdate() {
        return MySQLMultiUpdate.simple(null);
    }

    public static <C> MySQLUpdate._WithAndMultiUpdateSpec<C> multiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple(criteria);
    }

    public static MySQLUpdate._BatchWithAndMultiUpdateSpec<Void> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(null);
    }

    public static <C> MySQLUpdate._BatchWithAndMultiUpdateSpec<C> batchMultiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.batch(criteria);
    }


    public static MySQLDelete._WithAndSingleDeleteSpec<Void> singleDelete() {
        return MySQLSingleDelete.simple(null);
    }

    public static <C> MySQLDelete._WithAndSingleDeleteSpec<C> singleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.simple(criteria);
    }

    public static MySQLDelete._BatchWithAndSingleDeleteSpec<Void> batchSingleDelete() {
        return MySQLSingleDelete.batch(null);
    }

    public static <C> MySQLDelete._BatchWithAndSingleDeleteSpec<C> batchSingleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.batch(criteria);
    }

    public static MySQLDelete._WithAndMultiDeleteSpec<Void> multiDelete() {
        return MySQLMultiDelete.simple(null);
    }

    public static <C> MySQLDelete._WithAndMultiDeleteSpec<C> multiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.simple(criteria);
    }


    public static MySQLDelete._BatchWithAndMultiDeleteSpec<Void> batchMultiDelete() {
        return MySQLMultiDelete.batch(null);
    }

    public static <C> MySQLDelete._BatchWithAndMultiDeleteSpec<C> batchMultiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.batch(criteria);
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
        return SimpleWindow.standard(windowName, criteriaContext);
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
        return SimpleWindow.standard(windowName, context);
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
