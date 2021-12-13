package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.*;
import io.army.tx.Isolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public abstract class SQLs extends AbstractSQLs {


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


    public static <T extends IDomain> Insert.SubQueryInsertFieldSpec<T, Void> subQueryInsert(SingleTableMeta<T> table) {
        return null;
    }


    public static <T extends IDomain, C> Insert.SubQueryInsertFieldSpec<T, C> subQueryInsert(SingleTableMeta<T> table, C criteria) {
        return null;
    }


    public static <T extends IDomain> Insert.SubQueryInsertParentFieldSpec<T, Void> subQueryInsert(ChildTableMeta<T> table) {
        return null;
    }


    public static <T extends IDomain, C> Insert.SubQueryInsertParentFieldSpec<T, C> subQueryInsert(ChildTableMeta<T> table, C criteria) {
        return null;
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
    public static Update.BatchUpdateSpec<Void> batchUpdate() {
        return ContextualBatchUpdate.create();
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Update.BatchUpdateSpec<C> batchUpdate(C criteria) {
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
    public static Delete.BatchDomainDeleteSpec<Void> batchDelete() {
        return ContextualBatchDelete.create();
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Delete.BatchDomainDeleteSpec<C> batchDelete(C criteria) {
        return ContextualBatchDelete.create(criteria);
    }

    public static Query.SelectPartSpec<Select, EmptyObject> tableSelect() {
        return StandardContextualMultiSelect.build(EmptyObject.getInstance());
    }

    public static <C> Query.SelectPartSpec<Select, C> tableSelect(C criteria) {
        return StandardContextualMultiSelect.build(criteria);
    }

    public static Query.SelectPartSpec<SubQuery, Void> subQuery() {
        return SubQueries.subQuery();
    }

    public static <C> Query.SelectPartSpec<SubQuery, C> subQuery(C criteria) {
        return SubQueries.subQuery(criteria);
    }

    public static <C> Query.SelectPartSpec<RowSubQuery, C> rowSubQuery(C criteria) {
        return SubQueries.buildRowSubQuery(criteria);
    }

    public static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, C> columnSubQuery(Class<E> columnType, C criteria) {
        return SubQueries.buildColumnSubQuery(columnType, criteria);
    }

    public static <E> ColumnSubQuery.ColumnSelectionSpec<E, EmptyObject> columnSubQuery(Class<E> columnType) {
        return SubQueries.buildColumnSubQuery(columnType, EmptyObject.getInstance());
    }

    public static <E, C> ScalarSubQuery.ScalarSelectionSpec<E, C> scalarSubQuery(
            Class<E> javaType, MappingType mappingType, C criteria) {
        return SubQueries.buildScalarSubQuery(javaType, mappingType, criteria);
    }

    public static <E> ScalarSubQuery.ScalarSelectionSpec<E, EmptyObject> scalarSubQuery(
            Class<E> javaType, MappingType mappingType) {
        return SubQueries.buildScalarSubQuery(javaType, mappingType, EmptyObject.getInstance());
    }

    public static <E, C> ScalarSubQuery.ScalarSelectionSpec<E, C> scalarSubQuery(Class<E> javaType, C criteria) {
        return SubQueries.buildScalarSubQuery(javaType, _MappingFactory.getMapping(javaType), criteria);
    }

    public static <E> ScalarSubQuery.ScalarSelectionSpec<E, EmptyObject> scalarSubQuery(Class<E> javaType) {
        return SubQueries.buildScalarSubQuery(javaType, _MappingFactory.getMapping(javaType)
                , EmptyObject.getInstance());
    }

    /*################################## blow sql reference method ##################################*/

    public static <T extends IDomain, F> LogicalField<T, F> field(String tableAlias, FieldMeta<T, F> fieldMeta) {
        return CriteriaContextHolder.getContext()
                .aliasField(tableAlias, fieldMeta);
    }

    public static <E> Expression<E> ref(String subQueryAlias, String derivedFieldName) {
        return CriteriaContextHolder.getContext()
                .ref(subQueryAlias, derivedFieldName);
    }

    public static <E> Expression<E> ref(String subQueryAlias, String derivedFieldName, Class<E> selectionType) {
        return CriteriaContextHolder.getContext()
                .ref(subQueryAlias, derivedFieldName, selectionType);
    }

    /**
     * <p>
     * eg: {@link Query.UnionSpec#orderBy(SortPart)}
     * </p>
     */
    public static <E> Expression<E> composeRef(String selectionAlias) {
        return CriteriaContextHolder.getContext()
                .composeRef(selectionAlias);
    }

    public static <T extends IDomain> SelectionGroup group(TableMeta<T> tableMeta, String alias) {
        return SelectionGroups.buildTableGroup(alias, new ArrayList<>(tableMeta.fieldCollection()));
    }

    public static <T extends IDomain> SelectionGroup group(String tableAlias, List<FieldMeta<T, ?>> fieldMetaList) {
        return SelectionGroups.buildTableGroup(tableAlias, fieldMetaList);
    }

    public static List<SelectionGroup> childGroup(ChildTableMeta<?> childMeta, String parentAlias, String childAlias) {
        List<SelectionGroup> list = new ArrayList<>(2);
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

    public static IPredicate exists(SubQuery subQuery) {
        return UnaryPredicate.build(UnaryOperator.EXISTS, subQuery);
    }

    public static <C> IPredicate exists(Function<C, SubQuery> function) {
        return UnaryPredicate.build(
                UnaryOperator.EXISTS, function.apply(
                        CriteriaContextHolder.getContext().criteria()
                ));
    }

    public static IPredicate notExists(SubQuery subQuery) {
        return UnaryPredicate.build(UnaryOperator.NOT_EXISTS, subQuery);
    }

    public static <C> IPredicate notExists(Function<C, SubQuery> function) {
        return UnaryPredicate.build(
                UnaryOperator.NOT_EXISTS, function.apply(
                        CriteriaContextHolder.getContext().criteria()
                ));
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

    /**
     * package method.
     */
    @SuppressWarnings("unchecked")
    static <E> Expression<E> defaultKeyWord() {
        return (DefaultKeyWord<E>) DefaultKeyWord.INSTANCE;
    }

}
