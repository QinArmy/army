package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public abstract class SQLS extends AbstractSQLS {


    public static <T extends IDomain> UpdateAble.AliasAble<T, EmptyObject> update(TableMeta<T> tableMeta) {
        return new UpdateAbleImpl<>(tableMeta, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> UpdateAble.AliasAble<T, C> updateWithCriteria(
            TableMeta<T> tableMeta, C criteria) {
        return new UpdateAbleImpl<>(tableMeta, criteria);
    }

    public static DeleteAble.FromAble<EmptyObject> delete() {
        return new DeleteAbleImpl<>(EmptyObject.getInstance());
    }

    public static <C> DeleteAble.FromAble<C> prepareDelete(C criteria) {
        return new DeleteAbleImpl<>(criteria);
    }

    public static Select.SelectionAble<EmptyObject> prepareSelect() {
        return new ContextualSelectImpl<>(EmptyObject.getInstance());
    }

    public static <C> Select.SelectionAble<C> prepareSelect(C criteria) {
        return new ContextualSelectImpl<>(criteria);
    }

    public static <C> SubQuery.SubQuerySelectionAble<C> subQuery() {
        return new SubQueryAdaptor<>(
                CriteriaContextHolder.getContext().criteria()
        );
    }

    public static <C> RowSubQuery.RowSubQuerySelectionAble<C> rowSubQuery() {
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

    public static <T extends IDomain, F> AliasFieldMeta<T, F> field(String tableAlias, FieldMeta<T, F> fieldMeta) {
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
        return null;
    }

    public static SelectionGroup group(String tableAlias, List<FieldMeta<?, ?>> fieldMetaList) {
        return null;
    }

    public static SelectionGroup derivedGroup(String subQueryAlias) {
        return null;
    }

    public static SelectionGroup derivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return null;
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

    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static <E extends Number> Expression<E> abs(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ABS", x.mappingType(), x);
    }

    public static <E extends Number> Expression<Double> acos(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ACOS", MappingFactory.getDefaultMapping(Double.class), x);
    }


    public static <E extends Number> Expression<Double> asin(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ASIN", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("ATAN", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> atan(Expression<E> one, Expression<E> two) {
        return new Funcs.TwoArgumentFunc<>("ATAN", MappingFactory.getDefaultMapping(Double.class), one, two);
    }

    public static <E extends Number> Expression<Integer> cell(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("CELL", MappingFactory.getDefaultMapping(Integer.class), x);
    }

    public static <E extends Number> Expression<Long> cellAsLong(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("CELL", MappingFactory.getDefaultMapping(Long.class), x);
    }

    public static <E extends Number> Expression<String> conv(Expression<E> number, int fromBase, int toBase) {
        return new Funcs.ThreeArgumentFunc<>("CONV", MappingFactory.getDefaultMapping(String.class), number
                , constant(fromBase), constant(toBase));
    }

    public static <E extends Number> Expression<Double> cos(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("COS", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Double> cot(Expression<E> x) {
        return new Funcs.OneArgumentFunc<>("COT", MappingFactory.getDefaultMapping(Double.class), x);
    }

    public static <E extends Number> Expression<Long> crc32(Expression<E> expression) {
        return new Funcs.OneArgumentFunc<>("CRC32", MappingFactory.getDefaultMapping(Long.class), expression);
    }

    public static <E extends Number> Expression<Double> degrees(Expression<E> radian) {
        return new Funcs.OneArgumentFunc<>("DEGREES", MappingFactory.getDefaultMapping(Double.class), radian);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> exp(Expression<E> index) {
        return new Funcs.OneArgumentFunc<>("EXP", MappingFactory.getDefaultMapping(Double.class), index);
    }

    public static <E extends Number> Expression<Integer> floor(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("FLOOR", MappingFactory.getDefaultMapping(Integer.class), number);
    }

    public static <E extends Number> Expression<Long> floorAsLong(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("FLOOR", MappingFactory.getDefaultMapping(Long.class), number);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, Expression<E> decimal) {
        return new Funcs.TwoArgumentFunc<>("FORMAT", MappingFactory.getDefaultMapping(String.class), number, decimal);
    }

    public static <E extends Number> Expression<String> format(Expression<E> number, int decimal) {
        return new Funcs.TwoArgumentFunc<>("FORMAT", MappingFactory.getDefaultMapping(String.class)
                , number, constant(decimal));
    }

    public static <E extends Number> Expression<String> hex(Expression<E> number) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), number);
    }

    public static Expression<String> hex(Number number) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), constant(number));
    }

    public static Expression<String> hex(String numberText) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), constant(numberText));
    }

    public static Expression<String> hexForText(Expression<String> numberText) {
        return new Funcs.OneArgumentFunc<>("HEX", MappingFactory.getDefaultMapping(String.class), numberText);
    }

    /**
     * @see #exp(Expression)
     */
    public static <E extends Number> Expression<Double> ln(Expression<E> power) {
        return new Funcs.OneArgumentFunc<>("LN", MappingFactory.getDefaultMapping(Double.class), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log(Expression<E> power) {
        return new Funcs.OneArgumentFunc<>("LOG", MappingFactory.getDefaultMapping(Double.class), power);
    }

    public static <B extends Number, P extends Number> Expression<Double> log(Expression<B> bottomNumber
            , Expression<P> power) {
        return new Funcs.TwoArgumentFunc<>("LOG", MappingFactory.getDefaultMapping(Double.class), bottomNumber, power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log2(Expression<E> power) {
        return log(constant(2), power);
    }

    /**
     * @see #ln(Expression)
     */
    public static <E extends Number> Expression<Double> log10(Expression<E> power) {
        return log(constant(10), power);
    }

    public static <E extends Number> Expression<E> mod(Expression<E> dividend
            , Expression<E> divisor) {
        return new Funcs.TwoArgumentFunc<>("MOD", dividend.mappingType(), dividend, divisor);
    }


    public static Expression<Double> pi() {
        return new Funcs.NoArgumentFunc<>("PI", MappingFactory.getDefaultMapping(Double.class));
    }


    /*################################## blow date time function method ##################################*/

    public static Expression<LocalDateTime> now() {
        return new Funcs.NoArgumentFunc<>("NOW", MappingFactory.getDefaultMapping(LocalDateTime.class));
    }

    public static Expression<LocalDate> currentDate() {
        return new Funcs.NoArgumentFunc<>("CURRENT_DATE", MappingFactory.getDefaultMapping(LocalDate.class));
    }


}
