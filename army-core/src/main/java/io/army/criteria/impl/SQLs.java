package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.tx.Isolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is util class used to create standard sql statement.
 * </p>
 */
@SuppressWarnings({"unused"})
public abstract class SQLs extends SQLUtils {

    /**
     * protected constructor, application developer can extend this util class.
     */
    protected SQLs() {
        throw new UnsupportedOperationException();
    }


    public static <T extends IDomain> Insert.InsertOptionSpec<T, Void> standardInsert(TableMeta<T> targetTable) {
        return StandardValueInsert.create(targetTable, null);
    }

    /**
     * create a standard insert api object.
     * <p>
     * a standard insert api support blow {@link io.army.meta.MappingMode}:
     *     <ul>
     *         <li>{@link io.army.meta.SimpleTableMeta}</li>
     *         <li>{@link io.army.meta.ParentTableMeta},auto append {@link IPredicate} {@code discriminatorValue = 0}</li>
     *        <li>{@link io.army.meta.ChildTableMeta},but must in {@link Isolation#READ_COMMITTED }+ level environment.</li>
     *     </ul>
     * </p>
     * <p>
     *     <ul>
     *         <li>see {@code io.army.sync.GenericSyncApiSession#valueInsert(io.army.criteria.Insert, io.army.criteria.Visible)}</li>
     *         <li> see {@code io.army.reactive.GenericReactiveApiSession#valueInsert(io.army.criteria.Insert, io.army.criteria.Visible)}</li>
     *     </ul>
     * </p>
     *
     * @param targetTable will insert to table meta
     * @return a standard insert api object.
     */
    public static <T extends IDomain, C> Insert.InsertOptionSpec<T, C> standardInsert(TableMeta<T> targetTable, C criteria) {
        Objects.requireNonNull(criteria);
        return StandardValueInsert.create(targetTable, criteria);
    }

    public static Update.StandardUpdateSpec<Void> standardUpdate() {
        return StandardUpdate.simple(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update.StandardUpdateSpec<C> standardUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.simple(criteria);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static Update.StandardBatchUpdateSpec<Void> standardBatchUpdate() {
        return StandardUpdate.batch(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Update.StandardBatchUpdateSpec<C> standardBatchUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.batch(criteria);
    }

    public static Delete.StandardDeleteSpec<Void> standardDelete() {
        return StandardDelete.simple(null);
    }

    public static <C> Delete.StandardDeleteSpec<C> standardDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.simple(criteria);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static Delete.StandardBatchDeleteSpec<Void> standardBatchDelete() {
        return StandardDelete.batch(null);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Delete.StandardBatchDeleteSpec<C> standardBatchDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.batch(criteria);
    }

    public static StandardQuery.StandardSelectSpec<Void, Select> standardQuery() {
        return StandardSimpleQuery.query(null);
    }


    public static <C> StandardQuery.StandardSelectSpec<C, Select> standardQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.query(criteria);
    }

    public static StandardQuery.StandardSelectSpec<Void, SubQuery> standardSubQuery() {
        return StandardSimpleQuery.subQuery(null);
    }

    public static <C> StandardQuery.StandardSelectSpec<C, SubQuery> standardSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.subQuery(criteria);
    }

    public static StandardQuery.StandardSelectSpec<Void, RowSubQuery> standardRowSubQuery() {
        return StandardSimpleQuery.rowSubQuery(null);
    }

    public static <C> StandardQuery.StandardSelectSpec<C, RowSubQuery> standardRowSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.rowSubQuery(criteria);
    }

    public static StandardQuery.StandardSelectSpec<Void, ColumnSubQuery> standardColumnSubQuery() {
        return StandardSimpleQuery.columnSubQuery(null);
    }

    public static <C, E> StandardQuery.StandardSelectSpec<C, ColumnSubQuery> standardColumnSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.columnSubQuery(criteria);
    }

    public static <E> StandardQuery.StandardSelectSpec<Void, ScalarQueryExpression<E>> standardScalarSubQuery() {
        return StandardSimpleQuery.scalarSubQuery(null);
    }

    public static <E> StandardQuery.StandardSelectSpec<Void, ScalarQueryExpression<E>> standardScalarSubQuery(Class<E> type) {
        return StandardSimpleQuery.scalarSubQuery(null);
    }

    public static <C, E> StandardQuery.StandardSelectSpec<C, ScalarQueryExpression<E>> standardScalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.scalarSubQuery(criteria);
    }

    public static <C, E> StandardQuery.StandardSelectSpec<C, ScalarQueryExpression<E>> standardScalarSubQuery(Class<E> type, C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.scalarSubQuery(criteria);
    }


    /*################################## blow sql reference method ##################################*/

    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     */
    public static <T extends IDomain, F> QualifiedField<T, F> field(String tableAlias, FieldMeta<T, F> field) {
        return CriteriaContextStack.peek().qualifiedField(tableAlias, field);
    }

    public static <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName) {
        return CriteriaContextStack.peek().ref(subQueryAlias, derivedFieldName);
    }

    public static <E> DerivedField<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType) {
        return CriteriaContextStack.peek()
                .ref(subQueryAlias, derivedFieldName, selectionType);
    }

    public static <E> Expression<E> composeRef(String selectionAlias) {
        return CriteriaContextStack.peek()
                .composeRef(selectionAlias);
    }

    public static <T extends IDomain> SelectionGroup group(TableMeta<T> table, String alias) {
        return SelectionGroups.singleGroup(table, alias);
    }

    public static <T extends IDomain> SelectionGroup group(String tableAlias, List<FieldMeta<T, ?>> fieldList) {
        return SelectionGroups.singleGroup(tableAlias, fieldList);
    }


    public static <T extends IDomain> SelectionGroup parentGroup(ParentTableMeta<T> parent, String alias) {
        return SelectionGroups.parentGroup(parent, alias);
    }

    public static <T extends IDomain> List<SelectionGroup> childGroup(ChildTableMeta<T> childMeta, String parentAlias, String childAlias) {
        final List<SelectionGroup> list = new ArrayList<>(2);
        list.add(SelectionGroups.parentGroup(childMeta.parentMeta(), parentAlias));
        list.add(SelectionGroups.singleGroup(childMeta, childAlias));
        return list;
    }

    public static SelectionGroup derivedGroup(String subQueryAlias) {
        return SelectionGroups.buildDerivedGroup(subQueryAlias);
    }

    public static SelectionGroup derivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return SelectionGroups.buildDerivedGroup(subQueryAlias, derivedFieldNameList);
    }


    /*################################## blow sql key word operate method ##################################*/

    public static IPredicate exists(Supplier<SubQuery> supplier) {
        return UnaryPredicate.create(UnaryOperator.EXISTS, supplier.get());
    }

    public static <C> IPredicate exists(Function<C, SubQuery> function) {
        return UnaryPredicate.create(UnaryOperator.EXISTS, function.apply(CriteriaContextStack.getCriteria()));
    }

    public static IPredicate notExists(Supplier<SubQuery> supplier) {
        return UnaryPredicate.create(UnaryOperator.NOT_EXISTS, supplier.get());
    }

    public static <C> IPredicate notExists(Function<C, SubQuery> function) {
        return UnaryPredicate.create(UnaryOperator.NOT_EXISTS, function.apply(CriteriaContextStack.getCriteria()));
    }

    static <T extends IDomain> ExpressionRow<T> row(List<Expression<?>> columnList) {
        return new ExpressionRowImpl<>(null);
    }

    static <T extends IDomain, C> ExpressionRow<T> row(Function<C, List<Expression<?>>> function) {
       /* return new ExpressionRowImpl(function.apply(
                CriteriaContextHolder.getContext().criteria()
        ));*/
        return null;
    }


}
