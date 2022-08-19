package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.dialect._Constant;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.mapping.StringType;
import io.army.mapping._MappingFactory;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SingleParam;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is util class used to create standard sql statement.
 * </p>
 */
@SuppressWarnings({"unused"})
public abstract class SQLs extends StandardSyntax {


    /**
     * private constructor
     */
    private SQLs() {
    }


    public static final class Modifier implements SQLWords {
        public final String keyWords;

        /**
         * private constructor
         */
        private Modifier(String keyWords) {
            this.keyWords = keyWords;
        }

        @Override
        public String render() {
            return this.keyWords;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(SQLs.Modifier.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.keyWords.substring(1))
                    .toString();
        }

    }//Modifier

    public static final Modifier ALL = new Modifier(" ALL");

    public static final Modifier DISTINCT = new Modifier(" DISTINCT");


    public static Insert._StandardDomainOptionSpec<Void> domainInsert() {
        return StandardInserts.domainInsert(null);
    }

    public static <C> Insert._StandardDomainOptionSpec<C> domainInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardInserts.domainInsert(criteria);
    }


    public static Insert._StandardValueOptionSpec<Void> valueInsert() {
        return StandardInserts.valueInsert(null);
    }

    public static <C> Insert._StandardValueOptionSpec<C> valueInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardInserts.valueInsert(criteria);
    }


    public static Insert._StandardQueryInsertClause<Void> rowSetInsert() {
        return StandardInserts.rowSetInsert(null);
    }

    public static <C> Insert._StandardQueryInsertClause<C> rowSetInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardInserts.rowSetInsert(criteria);
    }

    public static Update._StandardDomainUpdateClause<Void> domainUpdate() {
        return StandardUpdate.simpleDomain(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update._StandardDomainUpdateClause<C> domainUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.simpleDomain(criteria);
    }

    public static Update._StandardSingleUpdateClause<Void> singleUpdate() {
        return StandardUpdate.simpleSingle(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update._StandardSingleUpdateClause<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.simpleSingle(criteria);
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @see #namedParam(DataField)
     * @see #namedParam(ParamMeta, String)
     * @see #namedParams(DataField, int)
     * @see #namedParams(ParamMeta, String, int)
     * @see #nullableNamedParam(DataField)
     * @see #nullableNamedParam(ParamMeta, String)
     */
    public static Update._StandardBatchDomainUpdateClause<Void> batchDomainUpdate() {
        return StandardUpdate.batchDomain(null);
    }

    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @param criteria a criteria object , map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedParam(DataField)
     * @see #namedParam(ParamMeta, String)
     * @see #namedParams(DataField, int)
     * @see #namedParams(ParamMeta, String, int)
     * @see #nullableNamedParam(DataField)
     * @see #nullableNamedParam(ParamMeta, String)
     */
    public static <C> Update._StandardBatchDomainUpdateClause<C> batchDomainUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.batchDomain(criteria);
    }

    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @see #namedParam(DataField)
     * @see #namedParam(ParamMeta, String)
     * @see #namedParams(DataField, int)
     * @see #namedParams(ParamMeta, String, int)
     * @see #nullableNamedParam(DataField)
     * @see #nullableNamedParam(ParamMeta, String)
     */
    public static Update._StandardBatchSingleUpdateClause<Void> batchSingleUpdate() {
        return StandardUpdate.batchSingle(null);
    }

    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @param criteria a criteria object , map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedParam(DataField)
     * @see #namedParam(ParamMeta, String)
     * @see #namedParams(DataField, int)
     * @see #namedParams(ParamMeta, String, int)
     * @see #nullableNamedParam(DataField)
     * @see #nullableNamedParam(ParamMeta, String)
     */
    public static <C> Update._StandardBatchSingleUpdateClause<C> batchSingleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.batchSingle(criteria);
    }

    public static Delete.StandardDeleteSpec<Void> domainDelete() {
        return StandardDelete.simple(null);
    }

    public static <C> Delete.StandardDeleteSpec<C> domainDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.simple(criteria);
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     *
     * @see #namedParam(DataField)
     * @see #namedParam(ParamMeta, String)
     * @see #namedParams(ParamMeta, String, int)
     * @see #nullableNamedParam(DataField)
     * @see #nullableNamedParam(ParamMeta, String)
     */
    public static Delete.StandardBatchDeleteSpec<Void> batchDomainDelete() {
        return StandardDelete.batch(null);
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     *
     * @param criteria a criteria object , map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedParam(DataField)
     * @see #namedParam(ParamMeta, String)
     * @see #namedParams(ParamMeta, String, int)
     * @see #nullableNamedParam(DataField)
     * @see #nullableNamedParam(ParamMeta, String)
     */
    public static <C> Delete.StandardBatchDeleteSpec<C> batchDomainDelete(C criteria) {
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
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static Expression _nonNullParam(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value == null) {
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), _Exceptions::expressionIsNull);
        } else if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = ParamExpression.single((TableField) type, value);
        } else {
            resultExpression = ParamExpression.single(type.paramMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static Expression _nullableParam(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = ParamExpression.single((TableField) type, value);
        } else {
            resultExpression = ParamExpression.single(type.paramMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method that is used by army developer.
     *
     * @param exp {@link Expression} or parameter
     */
    static ArmyExpression _nonNullExp(final @Nullable Object exp) {
        final Expression expression;
        if (exp == null) {
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), _Exceptions::expressionIsNull);
        } else if (exp instanceof Expression) {
            if (!(exp instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(CriteriaContextStack.peek());
            }
            expression = (Expression) exp;
        } else {
            expression = SQLs.param(exp);
        }
        return (ArmyExpression) expression;
    }

    /**
     * package method that is used by army developer.
     */
    static Expression _nonNullLiteral(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value == null) {
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), _Exceptions::expressionIsNull);
        } else if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = LiteralExpression.single((TableField) type, value);
        } else {
            resultExpression = LiteralExpression.single(type.paramMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method that is used by army developer.
     */
    static ArmyExpression _nullableLiteral(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value == null) {
            resultExpression = SQLs.nullWord();
        } else if (value instanceof Expression) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = LiteralExpression.single((TableField) type, value);
        } else {
            resultExpression = LiteralExpression.single(type.paramMeta(), value);
        }
        return (ArmyExpression) resultExpression;
    }

    public static Expression nullParam(ParamMeta type) {
        return ParamExpression.single(type, null);
    }

    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     */
    static Expression _nullParam() {
        return StringTypeNull.INSTANCE;
    }

    /**
     * <p>
     * Create strict param expression
     * </p>
     */
    public static Expression param(final Object value) {
        Objects.requireNonNull(value);
        return ParamExpression.single(_MappingFactory.getDefault(value.getClass()), value);
    }

    /**
     * <p>
     * Create strict param expression
     * </p>
     */
    public static Expression param(final ParamMeta paramMeta, final @Nullable Object value) {
        return ParamExpression.single(paramMeta, value);
    }

    /**
     * <p>
     * Create optimizing collection param expression
     * </p>
     */
    public static <E> Expression preferLiteralParams(ParamMeta paramMeta, Collection<E> value) {
        return ParamExpression.multi(paramMeta, value, true);
    }

    /**
     * <p>
     * Create optimizing collection param expression
     * </p>
     */
    public static <E> Expression preferLiteralParams(ParamMeta paramMeta, Supplier<Collection<E>> supplier) {
        return ParamExpression.multi(paramMeta, supplier.get(), true);
    }

    /**
     * <p>
     * Create optimizing collection param expression
     * </p>
     */
    public static <C, E> Expression preferLiteralParams(ParamMeta paramMeta, Function<C, Collection<E>> function) {
        return ParamExpression.multi(paramMeta, function.apply(CriteriaContextStack.getTopCriteria()), true);
    }

    /**
     * <p>
     * Create strict collection param expression
     * </p>
     */
    public static <E> Expression params(ParamMeta paramMeta, Collection<E> value) {
        return ParamExpression.multi(paramMeta, value, false);
    }

    /**
     * <p>
     * Create strict collection param expression
     * </p>
     */
    public static <E> Expression params(ParamMeta paramMeta, Supplier<Collection<E>> supplier) {
        return ParamExpression.multi(paramMeta, supplier.get(), false);
    }

    /**
     * <p>
     * Create strict collection param expression
     * </p>
     */
    public static <C, E> Expression params(ParamMeta paramMeta, Function<C, Collection<E>> function) {
        return ParamExpression.multi(paramMeta, function.apply(CriteriaContextStack.getTopCriteria()), false);
    }


    /**
     * <p>
     * Create nullable named parameter expression for batch update(or delete)
     * </p>
     *
     * @see Update._BatchSetClause
     */
    public static Expression nullableNamedParam(ParamMeta paramMeta, String name) {
        return ParamExpression.namedNullableSingle(paramMeta, name);
    }

    static Expression _nullableNamedParam(DataField field, String name) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (TableField) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return ParamExpression.namedNullableSingle(paramMeta, name);
    }


    /**
     * <p>
     * Create nullable named parameter expression for batch update(or delete)
     * </p>
     *
     * @see Update._BatchSetClause
     */
    public static Expression nullableNamedParam(final DataField field) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (TableField) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return ParamExpression.namedNullableSingle(paramMeta, field.fieldName());
    }


    /**
     * <p>
     * Create non-null named parameter expression for batch update(or delete)
     * </p>
     *
     * @see SQLs#batchDomainUpdate()
     * @see SQLs#batchDomainUpdate(Object)
     * @see SQLs#batchDomainDelete()
     * @see SQLs#batchDomainDelete(Object)
     */
    public static Expression namedParam(ParamMeta paramMeta, String name) {
        return ParamExpression.namedNonNullSingle(paramMeta, name);
    }

    static Expression _namedParam(DataField field, String name) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (TableField) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return ParamExpression.namedNonNullSingle(paramMeta, name);
    }


    /**
     * <p>
     * Create non-null named parameter expression for batch update(or delete)
     * </p>
     *
     * @see SQLs#batchDomainUpdate()
     * @see SQLs#batchDomainUpdate(Object)
     * @see SQLs#batchDomainDelete()
     * @see SQLs#batchDomainDelete(Object)
     */
    public static Expression namedParam(DataField field) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (TableField) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return ParamExpression.namedNonNullSingle(paramMeta, field.fieldName());
    }

    public static Expression namedParams(ParamMeta paramMeta, String name, int size) {
        return ParamExpression.namedMulti(paramMeta, name, size);
    }

    public static Expression namedParams(DataField field, int size) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (ParamMeta) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return ParamExpression.namedMulti(paramMeta, field.fieldName(), size);
    }

    public static Expression literal(Object value) {
        Objects.requireNonNull(value);
        return LiteralExpression.single(_MappingFactory.getDefault(value.getClass()), value);
    }

    public static Expression literal(ParamMeta paramMeta, Object value) {
        return LiteralExpression.single(paramMeta, value);
    }

    public static <E> Expression literals(List<E> valueList) {
        return LiteralExpression.multi(_MappingFactory.getDefault(valueList.get(0).getClass()), valueList);
    }

    public static <E> Expression literals(ParamMeta paramMeta, Collection<E> values) {
        return LiteralExpression.multi(paramMeta, values);
    }

    public static Expression namedLiteral(ParamMeta paramMeta, String name) {
        return LiteralExpression.namedSingle(paramMeta, name);
    }

    public static Expression nullableNamedLiteral(ParamMeta paramMeta, String name) {
        return LiteralExpression.nullableNamedSingle(paramMeta, name);
    }

    public static Expression namedLiterals(ParamMeta paramMeta, String name, int size) {
        return LiteralExpression.namedMulti(paramMeta, name, size);
    }

    static Expression star() {
        return StarLiteral.INSTANCE;
    }


    public static ItemPair itemPair(final DataField field, final @Nullable Object value) {
        return SQLs._itemPair(field, null, value);
    }


    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     *
     * @param value {@link Expression} or parameter.
     * @see Update._SetClause#setPairs(BiConsumer)
     * @see Update._SetClause#setPairs(Consumer)
     */
    static ArmyItemPair _itemPair(final DataField field, final @Nullable AssignOperator operator
            , final @Nullable Object value) {
        if (operator != null && value == null) {
            throw _Exceptions.expressionIsNull();
        }
        if (field instanceof TableField) {
            final TableField f = (TableField) field;
            if (f.updateMode() == UpdateMode.IMMUTABLE) {
                throw _Exceptions.immutableField(field);
            }
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                throw _Exceptions.armyManageField(f);
            }

            if (!f.nullable()
                    && (value == null
                    || (value instanceof Expression && ((ArmyExpression) value).isNullValue()))) {
                throw _Exceptions.nonNullField(f);
            }
        }

        final Expression valueExp;
        if (value instanceof Expression) {
            valueExp = (Expression) value;
        } else if (field instanceof TableField) {
            valueExp = SQLs.param((TableField) field, value);
        } else {
            valueExp = SQLs.param(field.paramMeta(), value);
        }
        final ArmyItemPair itemPair;
        if (operator == null) {
            itemPair = new FieldItemPair(field, (ArmyExpression) valueExp);
        } else {
            itemPair = new OperatorItemPair(field, operator, (ArmyExpression) valueExp);
        }
        return itemPair;
    }

    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     */
    static _ItemPair _itemExpPair(final DataField field, @Nullable Expression value) {
        assert value != null;
        return SQLs._itemPair(field, null, value);
    }

    public static ItemPair itemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
        return new RowItemPair(fieldList, subQuery);
    }

    public static ItemPair plusEqual(final DataField field, final @Nullable Object value) {
        return SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, value);
    }

    public static ItemPair minusEqual(final DataField field, final @Nullable Object value) {
        return SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, value);
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

    public static Expression trueWord() {
        return BooleanWord.TRUE;
    }

    public static Expression falseWord() {
        return BooleanWord.FALSE;
    }








    /*################################## blow sql reference method ##################################*/

    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     */
    public static <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return CriteriaContextStack.peek().qualifiedField(tableAlias, field);
    }

    public static DerivedField ref(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().ref(derivedTable, derivedFieldName);
    }

    public static DerivedField outerRef(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().outerRef(derivedTable, derivedFieldName);
    }


    /**
     * <p>
     * Reference a {@link  Selection} of current statement after selection list end,eg: ORDER BY clause.
     * </p>
     */
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

    public static CteItem refCte(String cteName) {
        return CriteriaContextStack.peek().refCte(cteName);
    }

    public static StandardQuery._StandardNestedLeftParenClause<Void> nestedItems() {
        return StandardNestedItems.create(null);
    }

    public static <C> StandardQuery._StandardNestedLeftParenClause<C> nestedItems(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardNestedItems.create(criteria);
    }

    public static StandardQuery._IfOnClause<Void> block(TableMeta<?> table, String tableAlias) {
        return DynamicBlock.standard(null, table, tableAlias);
    }

    public static <C> StandardQuery._IfOnClause<C> block(C criteria, TableMeta<?> table, String tableAlias) {
        Objects.requireNonNull(criteria);
        return DynamicBlock.standard(criteria, table, tableAlias);
    }


    public static StandardQuery._IfOnClause<Void> block(SubQuery subQuery, String tableAlias) {
        return DynamicBlock.standard(null, subQuery, tableAlias);
    }

    public static <C> StandardQuery._IfOnClause<C> block(C criteria, SubQuery subQuery, String tableAlias) {
        Objects.requireNonNull(criteria);
        return DynamicBlock.standard(null, subQuery, tableAlias);
    }

    public static <T> SelectionGroup group(TableMeta<T> table, String alias) {
        return SelectionGroups.singleGroup(table, alias);
    }

    public static <T> SelectionGroup group(String tableAlias, List<FieldMeta<T>> fieldList) {
        return SelectionGroups.singleGroup(tableAlias, fieldList);
    }

    /**
     * @return a group that no {@link ParentTableMeta#id()} column
     */
    public static <T> SelectionGroup groupWithoutId(TableMeta<T> table, String alias) {
        return SelectionGroups.groupWithoutId(table, alias);
    }

    public static <T> SelectionGroup childGroup(ChildTableMeta<T> child, String childAlias
            , String parentAlias) {
        return SelectionGroups.childGroup(child, childAlias, parentAlias);
    }


    public static SelectionGroup derivedGroup(String alias) {
        return SelectionGroups.derivedGroup(alias);
    }

    public static SelectionGroup derivedGroup(String alias, List<String> derivedFieldNameList) {
        if (derivedFieldNameList.size() == 0) {
            throw new CriteriaException("derivedFieldNameList must not empty");
        }
        return SelectionGroups.derivedGroup(alias, derivedFieldNameList);
    }

    public static Cte cte(String name, SubStatement subStatement) {
        return new CteImpl(name, subStatement);
    }

    public static Cte cte(String name, List<String> aliasLst, SubStatement subStatement) {
        return new CteImpl(name, aliasLst, subStatement);
    }



    /*################################## blow sql key word operate method ##################################*/

    public static IPredicate exists(Supplier<? extends SubQuery> supplier) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.EXISTS, supplier.get());
    }

    public static <C> IPredicate exists(Function<C, ? extends SubQuery> function) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.EXISTS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    public static IPredicate notExists(Supplier<? extends SubQuery> supplier) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.NOT_EXISTS, supplier.get());
    }

    public static <C> IPredicate notExists(Function<C, ? extends SubQuery> function) {
        final C criteria;
        criteria = CriteriaContextStack.getTopCriteria();
        return UnaryPredicate.fromSubQuery(UnaryOperator.NOT_EXISTS, function.apply(criteria));
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
            context.sqlBuilder().append(_Constant.SPACE_DEFAULT);
        }

        @Override
        public String toString() {
            return _Constant.SPACE_DEFAULT;
        }

    }// DefaultWord


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     * </p>
     */
    static final class NullWord extends NonOperationExpression implements SqlValueParam.SingleNonNamedValue {

        private static final NullWord INSTANCE = new NullWord();


        private NullWord() {
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_NULL);
        }

        @Override
        public ParamMeta paramMeta() {
            throw unsupportedOperation();
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return _Constant.SPACE_NULL;
        }


    }// NullWord


    private static final class StarLiteral extends NonOperationExpression {

        private static final StarLiteral INSTANCE = new StarLiteral();

        private StarLiteral() {
        }

        @Override
        public ParamMeta paramMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" *");
        }


    }//StarLiteral

    static final class BooleanWord extends OperationExpression {

        private static final BooleanWord TRUE = new BooleanWord(true);

        private static final BooleanWord FALSE = new BooleanWord(false);

        private final boolean value;

        private BooleanWord(boolean value) {
            this.value = value;
        }

        @Override
        public ParamMeta paramMeta() {
            return BooleanType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);
            if (this.value) {
                sqlBuilder.append(BooleanType.TRUE);
            } else {
                sqlBuilder.append(BooleanType.FALSE);
            }
        }

        @Override
        public String toString() {
            final String s;
            if (this.value) {
                s = _Constant.SPACE + BooleanType.TRUE;
            } else {
                s = _Constant.SPACE + BooleanType.FALSE;
            }
            return s;
        }


    }//BooleanWord


    static abstract class ArmyItemPair implements _ItemPair {

        final SetRightItem right;

        private ArmyItemPair(SetRightItem right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #itemPair(DataField, Object)
     */
    static class FieldItemPair extends ArmyItemPair implements _ItemPair._FieldItemPair {

        final DataField field;

        private FieldItemPair(DataField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final _SetClauseContext context) {
            final DataField field = this.field;
            //1. append left item
            context.appendSetLeftItem(field);
            //2. append operator
            if (this instanceof OperatorItemPair) {
                ((OperatorItemPair) this).operator
                        .appendOperator(context.parser().dialectMode(), field, context);
            } else {
                context.sqlBuilder()
                        .append(_Constant.SPACE_EQUAL);
            }
            //3. append right item
            ((_Expression) this.right).appendSql(context);
        }

        @Override
        public final DataField field() {
            return this.field;
        }

        @Override
        public final _Expression value() {
            return (_Expression) this.right;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.field);
            if (this instanceof OperatorItemPair) {
                builder.append(((OperatorItemPair) this).operator);
            } else {
                builder.append(_Constant.SPACE_EQUAL);
            }
            builder.append(this.right);
            return builder.toString();
        }

    }//FieldItemPair

    private static final class OperatorItemPair extends FieldItemPair {

        final AssignOperator operator;

        private OperatorItemPair(DataField field, AssignOperator operator, ArmyExpression value) {
            super(field, value);
            this.operator = operator;
        }


    }//OperatorItemPair

    static final class RowItemPair extends ArmyItemPair implements _ItemPair._RowItemPair {

        final List<DataField> fieldList;

        private RowItemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = CriteriaUtils.selectionCount(subQuery);
            if (fieldList.size() != selectionCount) {
                String m = String.format("Row column count[%s] and selection count[%s] of SubQuery not match."
                        , fieldList.size(), selectionCount);
                throw new CriteriaException(m);
            }
            final List<DataField> tempList = new ArrayList<>(fieldList.size());
            for (DataField field : fieldList) {
                if (!(field instanceof TableField)) {
                    tempList.add(field);
                    continue;
                }
                if (((TableField) field).updateMode() == UpdateMode.IMMUTABLE) {
                    throw _Exceptions.immutableField(field);
                }
                final String fieldName = field.fieldName();
                if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                    throw _Exceptions.armyManageField((TableField) field);
                }
                tempList.add(field);
            }
            this.fieldList = Collections.unmodifiableList(tempList);
        }

        @Override
        public void appendItemPair(final _SetClauseContext context) {
            final List<? extends DataField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //1. append left paren
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendSetLeftItem(fieldList.get(i));
            }
            //3. append right paren
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            sqlBuilder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            context.parser().rowSet((SubQuery) this.right, context);

        }

        @Override
        public List<? extends DataField> rowFieldList() {
            return this.fieldList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            //1. append left paren
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<? extends DataField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(fieldList.get(i));
            }
            //3. append right paren
            builder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            builder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            builder.append(this.right);
            return builder.toString();
        }

    }//RowItemPair

    private static final class StringTypeNull extends NonOperationExpression
            implements SingleParam, SqlValueParam.SingleValue {

        private static final StringTypeNull INSTANCE = new StringTypeNull();

        private StringTypeNull() {
        }

        @Override
        public ParamMeta paramMeta() {
            return StringType.INSTANCE;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }


    }//StringTypeNull


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

        final String name;

        final List<String> columnNameList;

        final SubStatement subStatement;

        private CteImpl(String name, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = Collections.emptyList();
            this.subStatement = subStatement;
        }


        private CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = _CollectionUtils.asUnmodifiableList(columnNameList);
            if (subStatement instanceof SubQuery) {
                final int columnAliasCount, selectionCount;
                columnAliasCount = columnNameList.size();
                selectionCount = CriteriaUtils.selectionCount((SubQuery) subStatement);
                if (columnAliasCount != selectionCount) {
                    String m;
                    m = String.format("cte column alias count[%s] and selection count[%s] of SubQuery not match."
                            , columnAliasCount, selectionCount);
                    throw new CriteriaException(m);
                }

            }
            this.subStatement = subStatement;
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
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<? extends SelectItem> selectItemList() {
            final SubStatement subStatement = this.subStatement;
            final List<? extends SelectItem> list;
            if (subStatement instanceof DerivedTable) {
                list = ((DerivedTable) subStatement).selectItemList();
            } else {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public Selection selection(final String derivedFieldName) {
            final SubStatement subStatement = this.subStatement;
            final Selection selection;
            if (subStatement instanceof DerivedTable) {
                selection = ((DerivedTable) subStatement).selection(derivedFieldName);
            } else {
                selection = null;
            }
            return selection;
        }


    }//CteImpl


}
