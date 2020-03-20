package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public abstract class SQLS extends AbstractSQLS {


    public static Update.SingleUpdateAble<EmptyObject> singleUpdate() {
        return new StandardContextualSingleUpdate<>(EmptyObject.getInstance());
    }

    public static <C> Update.SingleUpdateAble<C> singleUpdate(C criteria) {
        return new StandardContextualSingleUpdate<>(criteria);
    }

    public static Delete.SingleDeleteAble<EmptyObject> singleDelete() {
        return new StandardContextualSingleDelete<>(EmptyObject.getInstance());
    }

    public static <C> Delete.SingleDeleteAble<C> singleDelete(C criteria) {
        return new StandardContextualSingleDelete<>(criteria);
    }


    public static Select.SelectionAble<EmptyObject> singleSelect() {
        return new StandardContextualSingleSelect<>(EmptyObject.getInstance());
    }

    public static <C> Select.SelectionAble<C> singleSelect(C criteria) {
        return new StandardContextualSingleSelect<>(criteria);
    }

    public static Select.SelectionGroupAble<EmptyObject> multiSelect() {
        return new StandardContextualMultiSelect<>(EmptyObject.getInstance());
    }

    public static <C> Select.SelectionGroupAble<C> multiSelect(C criteria) {
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
            Class<E> columnType, MappingType mappingType) {
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

    public static <T extends IDomain, F> AliasFieldExp<T, F> field(String tableAlias, FieldMeta<T, F> fieldMeta) {
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

    public static SelectionGroup group(TableMeta<?> tableMeta, String alias) {
        return AbstractSelectionGroup.build(tableMeta, alias);
    }

    public static SelectionGroup group(String tableAlias, List<FieldMeta<?, ?>> fieldMetaList) {
        return AbstractSelectionGroup.buildForFieldList(tableAlias, fieldMetaList);
    }

    /**
     * package method.
     * package develop guarantee each element of fieldMetaList is {@link FieldMeta}
     * and elements belong to same {@link TableMeta}.
     */
    static SelectionGroup fieldGroup(String tableAlias, List<Selection> fieldMetaLis) {
        return AbstractSelectionGroup.buildForFields(tableAlias, fieldMetaLis);
    }

    public static SelectionGroup derivedGroup(String subQueryAlias) {
        return AbstractSelectionGroup.build(subQueryAlias);
    }

    public static SelectionGroup derivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return AbstractSelectionGroup.build(subQueryAlias, derivedFieldNameList);
    }


    /*################################## blow sql key word operate method ##################################*/

    public static IPredicate exists(SubQuery subQuery) {
        return new ExistsPredicate(subQuery);
    }

    public static <C> IPredicate exists(Function<C, SubQuery> function) {
        return new ExistsPredicate(function.apply(
                CriteriaContextHolder.getContext().criteria()
        ));
    }

    public static IPredicate notExists(SubQuery subQuery) {
        return new ExistsPredicate(true, subQuery);
    }

    public static <C> IPredicate notExists(Function<C, SubQuery> function) {
        return new ExistsPredicate(true, function.apply(
                CriteriaContextHolder.getContext().criteria()
        ));
    }

    public static Row row(List<Expression<?>> columnList) {
        return new RowImpl(columnList);
    }

    public static <C> Row row(Function<C, List<Expression<?>>> function) {
        return new RowImpl(function.apply(
                CriteriaContextHolder.getContext().criteria()
        ));
    }

}
