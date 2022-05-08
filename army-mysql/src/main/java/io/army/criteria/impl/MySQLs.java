package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQL80Query;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Objects;

public abstract class MySQLs extends MySQLSyntax {

    /**
     * protected constructor, application developer can extend this util class.
     */
    protected MySQLs() {
    }

    public static MySQL80Query._WithSpec<Void, Select> query() {
        return MySQL80SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL80Query._WithSpec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.simpleSelect(criteria);
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


    static MySQLUpdate._WithAndMultiUpdateSpec<Void> multiUpdate() {
        return MySQLMultiUpdate.simple(null);
    }

    static <C> MySQLUpdate._WithAndMultiUpdateSpec<C> multiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple(criteria);
    }

    static MySQLUpdate._BatchWithAndMultiUpdateSpec<Void> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(null);
    }

    static <C> MySQLUpdate._BatchWithAndMultiUpdateSpec<C> batchMultiUpdate(C criteria) {
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
        return MySQLMultiDelete.simple80(null);
    }

    public static <C> MySQLDelete._WithAndMultiDeleteSpec<C> multiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.simple80(criteria);
    }


    public static MySQLDelete._BatchWithAndMultiDeleteSpec<Void> batchMultiDelete() {
        return MySQLMultiDelete.batch80(null);
    }

    public static <C> MySQLDelete._BatchWithAndMultiDeleteSpec<C> batchMultiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.batch80(criteria);
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
