package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.tx.Isolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public abstract class SQLS extends AbstractSQLS {

    SQLS() {

    }

    /**
     * create a standard insert api object.
     * <p>
     * a standard insert api support blow {@link io.army.meta.MappingMode}:
     *     <ul>
     *         <li>{@link io.army.meta.MappingMode#SIMPLE}</li>
     *         <li>{@link io.army.meta.MappingMode#PARENT}</li>
     *        <li>{@link io.army.meta.MappingMode#CHILD},but must in {@link Isolation#READ_COMMITTED }+ level environment.</li>
     *     </ul>
     * </p>
     *
     * @param targetTable will insert to table meta
     * @return a standard insert api object.
     */
    public static <T extends IDomain> Insert.InsertIntoAble<T> multiInsert(TableMeta<T> targetTable) {
        return new StandardInsert<>(targetTable);
    }


    public static <T extends IDomain> Insert.SubQueryTargetFieldAble<T, EmptyObject> subQueryInsert(
            TableMeta<T> targetTable) {
        return new StandardContextualSubQueryInsert<>(targetTable, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> Insert.SubQueryTargetFieldAble<T, C> subQueryInsert(
            TableMeta<T> targetTable, C criteria) {
        return new StandardContextualSubQueryInsert<>(targetTable, criteria);
    }

    public static <T extends IDomain> Update.SingleUpdateAble<T, EmptyObject> singleUpdate(TableMeta<T> targetTable) {
        return new StandardContextualUpdate<>(targetTable, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> Update.SingleUpdateAble<T, C> singleUpdate(TableMeta<T> targetTable
            , C criteria) {
        return new StandardContextualUpdate<>(targetTable, criteria);
    }

    public static Delete.SingleDeleteAble<EmptyObject> singleDelete() {
        return StandardContextualSingleDelete.buildDelete(EmptyObject.getInstance());
    }

    public static <C> Delete.SingleDeleteAble<C> singleDelete(C criteria) {
        return StandardContextualSingleDelete.buildDelete(criteria);
    }

    public static Delete.SingleDeleteAble<EmptyObject> domainDelete(Object primaryKeyValue) {
        return StandardContextualSingleDelete.buildDomainDelete(primaryKeyValue, EmptyObject.getInstance());
    }

    public static <C> Delete.SingleDeleteAble<C> domainDelete(Object primaryKeyValue, C criteria) {
        return StandardContextualSingleDelete.buildDomainDelete(primaryKeyValue, criteria);
    }

    public static Select.SelectPartAble<EmptyObject> multiSelect() {
        return new StandardContextualMultiSelect<>(EmptyObject.getInstance());
    }

    public static <C> Select.SelectPartAble<C> multiSelect(C criteria) {
        return new StandardContextualMultiSelect<>(criteria);
    }

    public static <C> SubQuery.SubQuerySelectPartAble<C> subQuery() {
        return new SubQueryAdaptor<>(
                CriteriaContextHolder.getContext().criteria()
        );
    }

    public static <C> RowSubQuery.RowSubQuerySelectPartAble<C> rowSubQuery() {
        return new RowSubQueryAdaptor<>(
                CriteriaContextHolder.getContext().criteria()
        );
    }

    public static <E, C> ColumnSubQuery.ColumnSubQuerySelectionAble<E, C> columnSubQuery(Class<E> columnType) {
        return new ColumnSubQueryAdaptor<>(columnType,
                CriteriaContextHolder.getContext().criteria()
        );
    }

    public static <E, C> ScalarSubQuery.ScalarSubQuerySelectionAble<E, C> scalarSubQuery(
            Class<E> columnType, MappingMeta mappingType) {
        return new ScalarSubQueryAdaptor<>(columnType
                , mappingType
                , CriteriaContextHolder.getContext().criteria()
        );
    }

    public static <E> ScalarSubQuery.ScalarSubQuerySelectionAble<E, EmptyObject> scalarSubQuery(Class<E> columnType) {
        return new ScalarSubQueryAdaptor<>(columnType
                , MappingFactory.getDefaultMapping(columnType)
                , CriteriaContextHolder.getContext().criteria()
        );
    }

    /*################################## blow sql reference method ##################################*/

    public static <T extends IDomain, F> AliasField<T, F> field(String tableAlias, FieldMeta<T, F> fieldMeta) {
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
     * eg: {@link Select.UnionAble#orderBy(SortPart)}
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

    static Row row(List<Expression<?>> columnList) {
        return new RowImpl(columnList);
    }

    static <C> Row row(Function<C, List<Expression<?>>> function) {
        return new RowImpl(function.apply(
                CriteriaContextHolder.getContext().criteria()
        ));
    }

}
