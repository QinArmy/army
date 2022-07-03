package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.*;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Objects;

public abstract class MySQLs extends MySQLSyntax {


    private MySQLs() {
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static MySQLInsert._DomainOptionSpec<Void> domainInsert() {
        return MySQLInserts.domainInsert(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static <C> MySQLInsert._DomainOptionSpec<C> domainInsert(C criteria) {
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
    public static MySQLInsert._RowSetInsertIntoSpec<Void> rowSetInsert() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">MySQL 8.0 INSERT statement</a>
     */
    public static <C> MySQLInsert._RowSetInsertIntoSpec<C> rowSetInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static MySQLReplace._DomainOptionSpec<Void> domainReplace() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQLReplace._DomainOptionSpec<C> domainReplace(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static MySQLReplace._ValueReplaceOptionSpec<Void> valueReplace() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQLReplace._ValueReplaceOptionSpec<C> valueReplace(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static MySQLReplace._AssignmentOptionSpec<Void> assignmentReplace() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQLReplace._AssignmentOptionSpec<C> assignmentReplace(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static MySQLReplace._RowSetReplaceIntoSpec<Void> rowSetReplace() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQLReplace._RowSetReplaceIntoSpec<C> rowSetReplace(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


    public static MySQL80Query._WithSpec<Void, Select> query() {
        return MySQL80SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL80Query._WithSpec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.simpleSelect(criteria);
    }


    public static MySQLValues._ValuesStmtValues<Void> primaryValues() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQLValues._ValuesStmtValues<Void> primaryValues(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static MySQL80Query._WithSpec<Void, SubQuery> subQuery() {
        return MySQL80SimpleQuery.subQuery(false, null);
    }

    public static MySQL80Query._WithSpec<Void, SubQuery> lateralSubQuery() {
        return MySQL80SimpleQuery.subQuery(true, null);
    }

    public static <C> MySQL80Query._WithSpec<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(false, criteria);
    }

    public static <C> MySQL80Query._WithSpec<C, SubQuery> lateralSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(true, criteria);
    }


    public static MySQL80Query._WithSpec<Void, ScalarExpression> scalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(false, null);
    }

    public static MySQL80Query._WithSpec<Void, ScalarExpression> lateralScalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(true, null);
    }


    public static <C> MySQL80Query._WithSpec<C, ScalarExpression> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(false, criteria);
    }

    public static <C> MySQL80Query._WithSpec<C, ScalarExpression> lateralScalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(true, criteria);
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
        criteriaContext = CriteriaContextStack.peek();
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
        if (!_StringUtils.hasText(windowName)) {
            throw _Exceptions.namedWindowNoText();
        }
        final CriteriaContext criteriaContext;
        criteriaContext = CriteriaContextStack.peek();
        if (criteria != criteriaContext.criteria()) {
            throw new CriteriaException("Current criteria object don't match,please check criteria.");
        }
        return SimpleWindow.standard(windowName, criteriaContext);
    }

    public static MySQL80Query._NestedLeftBracketClause<Void> nestedItems() {
        return MySQLNestedItems.create(null);
    }

    public static <C> MySQL80Query._NestedLeftBracketClause<C> nestedItems(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLNestedItems.create(criteria);
    }


}
