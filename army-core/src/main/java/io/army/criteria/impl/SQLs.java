package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.mapping._MappingFactory;
import io.army.meta.*;
import io.army.stmt.StrictParamValue;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collection;
import java.util.Collections;
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
public abstract class SQLs extends StandardFunctions {

    /**
     * protected constructor, application developer can extend this util class.
     */
    protected SQLs() {
        throw new UnsupportedOperationException();
    }


    public static <T extends IDomain> Insert.StandardValueInsertSpec<T, Void> valueInsert(TableMeta<T> table) {
        return StandardValueInsert.create(table, null);
    }

    /**
     * create a standard insert api object.
     * <p>
     *     <ul>
     *         <li>see {@code io.army.sync.GenericSyncApiSession#valueInsert(io.army.criteria.Insert, io.army.criteria.Visible)}</li>
     *         <li> see {@code io.army.reactive.GenericReactiveApiSession#valueInsert(io.army.criteria.Insert, io.army.criteria.Visible)}</li>
     *     </ul>
     * </p>
     *
     * @param table will insert to table meta
     * @return a standard insert api object.
     */
    public static <T extends IDomain, C> Insert.StandardValueInsertSpec<T, C> valueInsert(TableMeta<T> table, C criteria) {
        Objects.requireNonNull(criteria);
        return StandardValueInsert.create(table, criteria);
    }

    public static Update.StandardUpdateSpec<Void> singleUpdate() {
        return StandardUpdate.simple(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update.StandardUpdateSpec<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.simple(criteria);
    }

    /**
     * @see #nullableNamedParam(String, ParamMeta)
     */
    public static Update.StandardBatchUpdateSpec<Void> batchUpdate() {
        return StandardUpdate.batch(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #nullableNamedParam(String, ParamMeta)
     */
    public static <C> Update.StandardBatchUpdateSpec<C> batchUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.batch(criteria);
    }

    public static Delete.StandardDeleteSpec<Void> singleDelete() {
        return StandardDelete.simple(null);
    }

    public static <C> Delete.StandardDeleteSpec<C> singleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.simple(criteria);
    }

    /**
     * @see #nullableNamedParam(String, ParamMeta)
     */
    public static Delete.StandardBatchDeleteSpec<Void> batchDelete() {
        return StandardDelete.batch(null);
    }

    /**
     * @see #nullableNamedParam(String, ParamMeta)
     */
    public static <C> Delete.StandardBatchDeleteSpec<C> batchDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.batch(criteria);
    }

    public static StandardQuery._StandardSelectClause<Void, Select> query() {
        return StandardSimpleQuery.query(null);
    }


    public static <C> StandardQuery._StandardSelectClause<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.query(criteria);
    }

    public static StandardQuery._StandardSelectClause<Void, SubQuery> subQuery() {
        return StandardSimpleQuery.subQuery(null);
    }

    public static <C> StandardQuery._StandardSelectClause<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.subQuery(criteria);
    }


    public static StandardQuery._StandardSelectClause<Void, ScalarExpression> scalarSubQuery() {
        return StandardSimpleQuery.scalarSubQuery(null);
    }


    public static <C> StandardQuery._StandardSelectClause<C, ScalarExpression> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.scalarSubQuery(criteria);
    }


    /**
     * package method
     *
     * @param value {@link Expression} or parameter
     */
    static Expression nonNullParam(final Expression type, final @Nullable Object value) {
        if (value == null) {
            throw new CriteriaException("Right operand of operator must be not null.");
        }
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof ParamMeta) {
            resultExpression = ParamExpression.create((ParamMeta) type, value);
        } else {
            resultExpression = ParamExpression.create(type.paramMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static Expression nullableParam(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof ParamMeta) {
            resultExpression = ParamExpression.create((ParamMeta) type, value);
        } else {
            resultExpression = ParamExpression.create(type.paramMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static ArmyExpression nullableParam(final @Nullable Object value) {
        final Expression expression;
        if (value == null) {
            expression = SQLs.nullParam();
        } else if (value instanceof Expression) {
            expression = (Expression) value;
        } else {
            expression = SQLs.param(value);
        }
        return (ArmyExpression) expression;
    }

    /**
     * package method that is used by army developer.
     *
     * @param exp {@link Expression} or parameter
     */
    static ArmyExpression _nonNullExp(final @Nullable Object exp) {
        final Expression expression;
        if (exp == null) {
            throw _Exceptions.expressionIsNull();
        } else if (exp instanceof Expression) {
            expression = (Expression) exp;
        } else {
            expression = SQLs.param(exp);
        }
        return (ArmyExpression) expression;
    }

    /**
     * package method
     */
    static Expression nonNullLiteral(final Expression type, final @Nullable Object value) {
        if (value == null) {
            throw new CriteriaException("Right operand of operator must be not null.");
        }
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof ParamMeta) {
            resultExpression = LiteralExpression.literal((ParamMeta) type, value);
        } else {
            resultExpression = LiteralExpression.literal(type.paramMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method
     */
    static Expression nullableLiteral(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value instanceof Expression) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            resultExpression = (Expression) value;
        } else if (value == null) {
            resultExpression = SQLs.nullWord();
        } else {
            resultExpression = LiteralExpression.literal(type.paramMeta(), value);
        }
        return resultExpression;
    }


    /**
     * <p>
     * Create strict param expression
     * </p>
     */
    public static Expression param(final Object value) {
        Objects.requireNonNull(value);
        return ParamExpression.create(_MappingFactory.getDefault(value.getClass()), value);
    }

    public static Expression nullParam() {
        return AnyTypeNull.INSTANCE;
    }

    /**
     * <p>
     * Create strict param expression
     * </p>
     */
    public static Expression param(final ParamMeta paramMeta, final @Nullable Object value) {
        return ParamExpression.create(paramMeta, value);
    }

    /**
     * <p>
     * Create optimizing collection param expression
     * </p>
     */
    public static <E> Expression optimizingParams(ParamMeta paramMeta, Collection<E> value) {
        return CollectionParamExpression.optimizing(paramMeta, value);
    }

    /**
     * <p>
     * Create optimizing collection param expression
     * </p>
     */
    public static <E> Expression optimizingParams(ParamMeta paramMeta, Supplier<Collection<E>> supplier) {
        return CollectionParamExpression.optimizing(paramMeta, supplier.get());
    }

    /**
     * <p>
     * Create optimizing collection param expression
     * </p>
     */
    public static <C, E> Expression optimizingParams(ParamMeta paramMeta, Function<C, Collection<E>> function) {
        return CollectionParamExpression.optimizing(paramMeta, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    /**
     * <p>
     * Create strict collection param expression
     * </p>
     */
    public static <E> Expression params(ParamMeta paramMeta, Collection<E> value) {
        return CollectionParamExpression.strict(paramMeta, value);
    }

    /**
     * <p>
     * Create strict collection param expression
     * </p>
     */
    public static <E> Expression params(ParamMeta paramMeta, Supplier<Collection<E>> supplier) {
        return CollectionParamExpression.strict(paramMeta, supplier.get());
    }

    /**
     * <p>
     * Create strict collection param expression
     * </p>
     */
    public static <C, E> Expression params(ParamMeta paramMeta, Function<C, Collection<E>> function) {
        return CollectionParamExpression.strict(paramMeta, function.apply(CriteriaContextStack.getTopCriteria()));
    }


    /**
     * <p>
     * Create nullable named parameter expression for batch update(or delete)
     * </p>
     *
     * @see io.army.criteria.Update.BatchSetClause
     */
    public static Expression nullableNamedParam(String name, ParamMeta paramMeta) {
        return NamedParamImpl.nullable(name, paramMeta);
    }

    /**
     * <p>
     * Create nullable named parameter expression for batch update(or delete)
     * </p>
     *
     * @see io.army.criteria.Update.BatchSetClause
     */
    public static Expression nullableNamedParam(TableField<?> field) {
        return NamedParamImpl.nullable(field.fieldName(), field);
    }


    /**
     * <p>
     * Create non-null named parameter expression for batch update(or delete)
     * </p>
     *
     * @see SQLs#batchUpdate()
     * @see SQLs#batchUpdate(Object)
     * @see SQLs#batchDelete()
     * @see SQLs#batchDelete(Object)
     */
    public static Expression namedParam(String name, ParamMeta paramMeta) {
        return NamedParamImpl.nonNull(name, paramMeta);
    }

    /**
     * <p>
     * Create non-null named parameter expression for batch update(or delete)
     * </p>
     *
     * @see SQLs#batchUpdate()
     * @see SQLs#batchUpdate(Object)
     * @see SQLs#batchDelete()
     * @see SQLs#batchDelete(Object)
     */
    public static Expression namedParam(TableField<?> field) {
        return NamedParamImpl.nonNull(field.fieldName(), field);
    }

    public static Expression literal(Object value) {
        Objects.requireNonNull(value);
        return LiteralExpression.literal(_MappingFactory.getDefault(value.getClass()), value);
    }

    public static Expression literal(ParamMeta paramMeta, Object value) {
        return LiteralExpression.literal(paramMeta, value);
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static ArmySortItem _nonNullSortItem(@Nullable Object value) {
        final SortItem sortItem;
        if (value == null) {
            throw _Exceptions.expressionIsNull();
        } else if (value instanceof SortItem) {
            sortItem = (SortItem) value;
        } else {
            sortItem = SQLs.literal(value);
        }
        return (ArmySortItem) sortItem;
    }

    /**
     * @param value {@link Expression} or parameter.
     * @see Update.SimpleSetClause#setPairs(List)
     */
    public static ItemPair itemPair(FieldMeta<?> field, @Nullable Object value) {
        final Expression valueExp;
        if (value instanceof Expression) {
            valueExp = (Expression) value;
        } else {
            valueExp = SQLs.param(field, value);
        }
        return new ItemPairImpl(field, valueExp);
    }

    public static ExpressionPair expPair(final Object first, final Object second) {
        final Expression firstExp, secondExp;
        if (first instanceof Expression) {
            firstExp = (Expression) first;
        } else {
            firstExp = SQLs.param(first);
        }
        if (second instanceof Expression) {
            secondExp = (Expression) second;
        } else {
            secondExp = SQLs.param(second);
        }
        return new ExpressionPairImpl(firstExp, secondExp);
    }

    public static ExpressionPair expPair(ParamMeta paramMeta, final Object first, final Object second) {
        final Expression firstExp, secondExp;
        if (first instanceof Expression) {
            firstExp = (Expression) first;
        } else {
            firstExp = SQLs.param(paramMeta, first);
        }
        if (second instanceof Expression) {
            secondExp = (Expression) second;
        } else {
            secondExp = SQLs.param(paramMeta, second);
        }
        return new ExpressionPairImpl(firstExp, secondExp);
    }


    /**
     * @return DEFAULT expression that output key word {@code DEFAULT}.
     */
    public static Expression defaultWord() {
        return SQLs.DefaultWord.INSTANCE;
    }


    /**
     * @return NULL expression that output key word {@code NULL}.
     */
    public static Expression nullWord() {
        return SQLs.NullWord.INSTANCE;
    }


    /*################################## blow sql reference method ##################################*/

    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     */
    public static <T extends IDomain> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return CriteriaContextStack.peek().qualifiedField(tableAlias, field);
    }

    public static DerivedField ref(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().ref(derivedTable, derivedFieldName);
    }


    public static Expression ref(String selectionAlias) {
        return CriteriaContextStack.peek().ref(selectionAlias);
    }

    /**
     * <p>
     * Reference session variable.
     * </p>
     *
     * @throws CriteriaException when var not exists
     */
    public static VarExpression var(String varName) {
        return CriteriaContextStack.root().var(varName);
    }


    /**
     * <p>
     * Create session variable.
     * </p>
     *
     * @throws CriteriaException when var exists.
     */
    public static VarExpression createVar(String varName, ParamMeta paramMeta)
            throws CriteriaException {
        return CriteriaContextStack.root().createVar(varName, paramMeta);
    }


    public static <T extends IDomain> SelectionGroup group(TableMeta<T> table, String alias) {
        return SelectionGroups.singleGroup(table, alias);
    }

    public static <T extends IDomain> SelectionGroup group(String tableAlias, List<FieldMeta<T>> fieldList) {
        return SelectionGroups.singleGroup(tableAlias, fieldList);
    }

    /**
     * @return a group that no {@link ParentTableMeta#id()} column
     */
    public static <T extends IDomain> SelectionGroup groupWithoutId(TableMeta<T> table, String alias) {
        return SelectionGroups.groupWithoutId(table, alias);
    }

    public static <T extends IDomain> SelectionGroup childGroup(ChildTableMeta<T> child, String childAlias
            , String parentAlias) {
        return SelectionGroups.childGroup(child, childAlias, parentAlias);
    }


    public static SelectionGroup derivedGroup(String subQueryAlias) {
        return SelectionGroups.buildDerivedGroup(subQueryAlias);
    }

    public static SelectionGroup derivedGroup(String subQueryAlias, List<String> derivedFieldNameList) {
        return SelectionGroups.buildDerivedGroup(subQueryAlias, derivedFieldNameList);
    }

    public static Cte cte(String name, Supplier<? extends SubQuery> supplier) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        return new CteImpl(name, subQuery);
    }

    public static Cte cte(String name, List<String> columnNameList, Supplier<? extends SubQuery> supplier) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        return new CteImpl(name, columnNameList, subQuery);
    }

    public static <C> Cte cte(String name, Function<C, ? extends SubQuery> function) {
        final SubQuery subQuery;
        subQuery = function.apply(CriteriaContextStack.getTopCriteria());
        assert subQuery != null;
        return new CteImpl(name, subQuery);
    }

    public static <C> Cte cte(String name, List<String> columnNameList, Function<C, ? extends SubQuery> function) {
        final SubQuery subQuery;
        subQuery = function.apply(CriteriaContextStack.getTopCriteria());
        assert subQuery != null;
        return new CteImpl(name, columnNameList, subQuery);
    }

    /**
     * package method
     */
    static <C> Cte cte(String name, SubQuery subQuery) {
        return new CteImpl(name, subQuery);
    }




    /*################################## blow sql key word operate method ##################################*/

    public static IPredicate exists(Supplier<SubQuery> supplier) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.EXISTS, supplier.get());
    }

    public static <C> IPredicate exists(Function<C, SubQuery> function) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.EXISTS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    public static IPredicate notExists(Supplier<SubQuery> supplier) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.NOT_EXISTS, supplier.get());
    }

    public static <C> IPredicate notExists(Function<C, SubQuery> function) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.NOT_EXISTS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    static <T extends IDomain> ExpressionRow row(List<Expression> columnList) {
        return new ExpressionRowImpl(null);
    }

    static <T extends IDomain, C> ExpressionRow row(Function<C, List<Expression>> function) {
       /* return new ExpressionRowImpl(function.apply(
                CriteriaContextHolder.getContext().criteria()
        ));*/
        return null;
    }


    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * </p>
     *
     * @param <E> The java type The expression thant reference kwy word {@code DEFAULT}
     */
    static final class DefaultWord<E> extends NonOperationExpression {

        private static final DefaultWord<?> INSTANCE = new DefaultWord<>();

        private DefaultWord() {
        }


        @Override
        public ParamMeta paramMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" DEFAULT");
        }

        @Override
        public String toString() {
            return " DEFAULT";
        }

    }// DefaultWord


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     * </p>
     */
    static final class NullWord extends NonOperationExpression {

        private static final NullWord INSTANCE = new NullWord();


        private NullWord() {
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.sqlBuilder().append(Constant.SPACE_NULL);
        }

        @Override
        public ParamMeta paramMeta() {
            throw unsupportedOperation();
        }

        @Override
        public String toString() {
            return Constant.SPACE_NULL;
        }


    }// NullWord


    /**
     * @see #itemPair(FieldMeta, Object)
     */
    static final class ItemPairImpl implements ItemPair {

        final SetLeftItem left;

        final SetRightItem right;

        private ItemPairImpl(FieldMeta<?> left, Expression right) {
            this.left = left;
            this.right = right;
        }

        private ItemPairImpl(Row left, SubQuery right) {
            this.left = left;
            this.right = right;
        }


        @Override
        public SetLeftItem left() {
            return this.left;
        }

        @Override
        public SetRightItem right() {
            return this.right;
        }

    }//ItemPairImpl

    private static final class AnyTypeNull extends NonOperationExpression
            implements StrictParamValue, ValueExpression {

        private static final AnyTypeNull INSTANCE = new AnyTypeNull();

        private AnyTypeNull() {
        }

        @Override
        public ParamMeta paramMeta() {
            return StringType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

    }//NullParam


    /**
     * @see #expPair(Object, Object)
     */
    private static final class ExpressionPairImpl implements ExpressionPair {

        private final Expression first;

        private final Expression second;

        private ExpressionPairImpl(Expression first, Expression second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public Expression first() {
            return this.first;
        }

        @Override
        public Expression second() {
            return this.second;
        }
    }//BetweenPair

    static final class CteImpl implements Cte {

        private final String name;

        private final List<String> columnNameList;

        private final SubQuery subQuery;

        private CteImpl(String name, SubQuery subQuery) {
            this.name = name;
            this.columnNameList = Collections.emptyList();
            this.subQuery = subQuery;
        }


        private CteImpl(String name, List<String> columnNameList, SubQuery subQuery) {
            this.name = name;
            this.columnNameList = _CollectionUtils.asUnmodifiableList(columnNameList);
            this.subQuery = subQuery;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public List<String> columnNameList() {
            return this.columnNameList;
        }

        @Override
        public SubQuery subQuery() {
            return this.subQuery;
        }

    }//CteImpl


}
