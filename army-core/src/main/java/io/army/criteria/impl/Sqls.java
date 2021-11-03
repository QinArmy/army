package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.mapping.MappingFactory;
import io.army.mapping.MappingType;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.tx.Isolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public abstract class Sqls extends AbstractSQLS {

    Sqls() {

    }

    /**
     * create a standard insert api object.
     * <p>
     * a standard insert api support blow {@link io.army.meta.MappingMode}:
     *     <ul>
     *         <li>{@link io.army.meta.MappingMode#SIMPLE}</li>
     *         <li>{@link io.army.meta.MappingMode#PARENT},auto append {@link IPredicate} {@code discriminatorValue = 0}</li>
     *        <li>{@link io.army.meta.MappingMode#CHILD},but must in {@link Isolation#READ_COMMITTED }+ level environment.</li>
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
    public static <T extends IDomain> Insert.InsertIntoSpec<T> multiInsert(TableMeta<T> targetTable) {
        return StandardInsert.build(targetTable);
    }

    /**
     * create a standard batch insert api object.
     * <p>
     * a standard insert api support blow {@link io.army.meta.MappingMode}:
     *     <ul>
     *         <li>{@link io.army.meta.MappingMode#SIMPLE}</li>
     *         <li>{@link io.army.meta.MappingMode#PARENT},auto append {@link IPredicate} {@code discriminatorValue = 0}</li>
     *        <li>{@link io.army.meta.MappingMode#CHILD},but must in {@link Isolation#READ_COMMITTED }+ level environment.</li>
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
    public static <T extends IDomain> Insert.InsertIntoSpec<T> batchInsert(TableMeta<T> targetTable) {
        return StandardBatchInsert.build(targetTable);
    }

    public static <T extends IDomain> Insert.SubQueryTargetFieldSpec<T, EmptyObject> subQueryInsert(
            TableMeta<T> targetTable) {
        return StandardContextualSubQueryInsert.build(targetTable, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> Insert.SubQueryTargetFieldSpec<T, C> subQueryInsert(
            TableMeta<T> targetTable, C criteria) {
        return StandardContextualSubQueryInsert.build(targetTable, criteria);
    }

    public static <T extends IDomain> Insert.ParentSubQueryTargetFieldSpec<T, EmptyObject> subQueryInsert(
            ChildTableMeta<T> tableMeta) {
        return StandardContextualChildSubQueryInsert.build(tableMeta, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> Insert.ParentSubQueryTargetFieldSpec<T, C> subQueryInsert(
            ChildTableMeta<T> tableMeta, C criteria) {
        return StandardContextualChildSubQueryInsert.build(tableMeta, criteria);
    }

    public static <T extends IDomain> Update.SingleUpdateSpec<T, EmptyObject> singleUpdate(TableMeta<T> targetTable) {
        return StandardContextualUpdate.build(targetTable, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> Update.SingleUpdateSpec<T, C> singleUpdate(TableMeta<T> targetTable
            , C criteria) {
        return StandardContextualUpdate.build(targetTable, criteria);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static <T extends IDomain> Update.BatchUpdateSpec<T, EmptyObject> batchSingleUpdate(
            TableMeta<T> targetTable) {
        return StandardContextualBatchUpdate.build(targetTable, EmptyObject.getInstance());
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static <T extends IDomain, C> Update.BatchUpdateSpec<T, C> batchSingleUpdate(TableMeta<T> targetTable
            , C criteria) {
        return StandardContextualBatchUpdate.build(targetTable, criteria);
    }

    public static Delete.SingleDeleteSpec<EmptyObject> singleDelete() {
        return StandardContextualDelete.buildDelete(EmptyObject.getInstance());
    }

    public static <C> Delete.SingleDeleteSpec<C> singleDelete(C criteria) {
        return StandardContextualDelete.buildDelete(criteria);
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static Delete.BatchSingleDeleteSpec<EmptyObject> batchSingleDelete() {
        return StandardContextualBatchDelete.build(EmptyObject.getInstance());
    }

    /**
     * @see #namedParam(String, ParamMeta)
     */
    public static <C> Delete.BatchSingleDeleteSpec<C> batchSingleDelete(C criteria) {
        return StandardContextualBatchDelete.build(criteria);
    }

    public static Query.SelectPartSpec<Select, EmptyObject> multiSelect() {
        return StandardContextualMultiSelect.build(EmptyObject.getInstance());
    }

    public static <C> Query.SelectPartSpec<Select, C> multiSelect(C criteria) {
        return StandardContextualMultiSelect.build(criteria);
    }

    public static <C> Query.SelectPartSpec<SubQuery, C> subQuery(C criteria) {
        return StandardSubQueries.build(criteria);
    }

    public static <C> Query.SelectPartSpec<RowSubQuery, C> rowSubQuery(C criteria) {
        return StandardSubQueries.buildRowSubQuery(criteria);
    }

    public static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, C> columnSubQuery(Class<E> columnType, C criteria) {
        return StandardSubQueries.buildColumnSubQuery(columnType, criteria);
    }

    public static <E> ColumnSubQuery.ColumnSelectionSpec<E, EmptyObject> columnSubQuery(Class<E> columnType) {
        return StandardSubQueries.buildColumnSubQuery(columnType, EmptyObject.getInstance());
    }

    public static <E, C> ScalarSubQuery.ScalarSelectionSpec<E, C> scalarSubQuery(
            Class<E> javaType, MappingType mappingType, C criteria) {
        return StandardSubQueries.buildScalarSubQuery(javaType, mappingType, criteria);
    }

    public static <E> ScalarSubQuery.ScalarSelectionSpec<E, EmptyObject> scalarSubQuery(
            Class<E> javaType, MappingType mappingType) {
        return StandardSubQueries.buildScalarSubQuery(javaType, mappingType, EmptyObject.getInstance());
    }

    public static <E, C> ScalarSubQuery.ScalarSelectionSpec<E, C> scalarSubQuery(Class<E> javaType, C criteria) {
        return StandardSubQueries.buildScalarSubQuery(javaType, MappingFactory.getDefaultMapping(javaType), criteria);
    }

    public static <E> ScalarSubQuery.ScalarSelectionSpec<E, EmptyObject> scalarSubQuery(Class<E> javaType) {
        return StandardSubQueries.buildScalarSubQuery(javaType, MappingFactory.getDefaultMapping(javaType)
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
        list.add(Sqls.group(childMeta.parentMeta(), parentAlias));
        list.add(Sqls.group(childMeta, childAlias));
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

}
