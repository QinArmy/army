package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
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


    public static <T extends IDomain> Insert.InsertOptionSpec<T, Void> domainInsert(TableMeta<T> targetTable) {
        return ContextualValueInsert.create(targetTable);
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
    public static <T extends IDomain, C> Insert.InsertOptionSpec<T, C> domainInsert(TableMeta<T> targetTable, C criteria) {
        return ContextualValueInsert.create(targetTable, criteria);
    }

    public static Update.DomainUpdateSpec<Void> domainUpdate() {
        return ContextualUpdate.create();
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update.DomainUpdateSpec<C> domainUpdate(C criteria) {
        return ContextualUpdate.create(criteria);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static Update.BatchUpdateSpec<Void> batchDomainUpdate() {
        return ContextualBatchUpdate.create();
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Update.BatchUpdateSpec<C> batchDomainUpdate(C criteria) {
        return ContextualBatchUpdate.create(criteria);
    }

    public static Delete.DomainDeleteSpec<Void> domainDelete() {
        return ContextualDelete.create();
    }

    public static <C> Delete.DomainDeleteSpec<C> domainDelete(C criteria) {
        return ContextualDelete.create(criteria);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static Delete.BatchDomainDeleteSpec<Void> batchDomainDelete() {
        return ContextualBatchDelete.create();
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Delete.BatchDomainDeleteSpec<C> batchDomainDelete(C criteria) {
        return ContextualBatchDelete.create(criteria);
    }

    public static Query.SelectPartSpec<Select, EmptyObject> tableSelect() {
        return StandardSelect.create(EmptyObject.getInstance());
    }


    public static <C> Query.SelectPartSpec<Select, C> tableSelect(C nonNullCriteria) {
        Objects.requireNonNull(nonNullCriteria);
        return StandardSelect.create(nonNullCriteria);
    }

    public static Query.SelectPartSpec<SubQuery, Void> subQuery() {
        return StandardSubQueries.subQuery(null);
    }

    public static <C> Query.SelectPartSpec<SubQuery, C> subQuery(C nonNullCriteria) {
        Objects.requireNonNull(nonNullCriteria);
        return StandardSubQueries.subQuery(nonNullCriteria);
    }

    public static Query.SelectPartSpec<RowSubQuery, Void> rowSubQuery() {
        return StandardSubQueries.rowSubQuery(null);
    }

    public static <C> Query.SelectPartSpec<RowSubQuery, C> rowSubQuery(C nonNullCriteria) {
        Objects.requireNonNull(nonNullCriteria);
        return StandardSubQueries.rowSubQuery(nonNullCriteria);
    }

    public static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, ColumnSubQuery<E>, Void> columnSubQuery() {
        return StandardSubQueries.columnSubQuery(null);
    }

    public static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, ColumnSubQuery<E>, C> columnSubQuery(C nonNullCriteria) {
        Objects.requireNonNull(nonNullCriteria);
        return StandardSubQueries.columnSubQuery(nonNullCriteria);
    }

    public static <E> ColumnSubQuery.ColumnSelectionSpec<E, ScalarExpression<E>, Void> scalarSubQuery() {
        return StandardSubQueries.scalarSubQuery(null);
    }

    public static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, ScalarExpression<E>, C> scalarSubQuery(C nonNullCriteria) {
        Objects.requireNonNull(nonNullCriteria);
        return StandardSubQueries.scalarSubQuery(nonNullCriteria);
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

    /**
     * <p>
     * eg: {@link Query.UnionSpec#orderBy(SortPart)}
     * </p>
     */
    public static <E> Expression<E> composeRef(String selectionAlias) {
        return CriteriaContextStack.peek()
                .composeRef(selectionAlias);
    }

    public static <T extends IDomain> SelectionGroup group(TableMeta<T> tableMeta, String alias) {
        return SelectionGroups.buildTableGroup(alias, new ArrayList<>(tableMeta.fieldCollection()));
    }

    public static <T extends IDomain> SelectionGroup group(String tableAlias, List<FieldMeta<T, ?>> fieldMetaList) {
        return SelectionGroups.buildTableGroup(tableAlias, fieldMetaList);
    }

    public static List<SelectionGroup> childGroup(ChildTableMeta<?> childMeta, String parentAlias, String childAlias) {
        final List<SelectionGroup> list = new ArrayList<>(2);
        list.add(SQLs.group(childMeta.parentMeta(), parentAlias));
        list.add(SQLs.group(childMeta, childAlias));
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
