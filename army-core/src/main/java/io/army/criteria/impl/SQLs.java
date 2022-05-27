package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Query;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect._UpdateContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.mapping._MappingFactory;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.StrictParamValue;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

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
public abstract class SQLs extends Functions {

    /**
     * protected constructor, application developer can extend this util class.
     */
    protected SQLs() {
        throw new UnsupportedOperationException();
    }


    public static <T extends IDomain> Insert._StandardOptimizingOptionSpec<Void, T> valueInsert(TableMeta<T> table) {
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
    public static <T extends IDomain, C> Insert._StandardOptimizingOptionSpec<C, T> valueInsert(TableMeta<T> table, C criteria) {
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
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static Expression _nonNullParam(final Expression type, final @Nullable Object value) {
        if (value == null) {
            throw _Exceptions.expressionIsNull();
        }
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = ParamExpression.create((TableField) type, value);
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
    static ArmyExpression _nullableParam(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = ParamExpression.create((TableField) type, value);
        } else {
            resultExpression = ParamExpression.create(type.paramMeta(), value);
        }
        return (ArmyExpression) resultExpression;
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static ArmyExpression _nullableParam(final @Nullable Object value) {
        final Expression expression;
        if (value == null) {
            expression = StringTypeNull.INSTANCE;
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
     * package method that is used by army developer.
     */
    static ArmyExpression _nonNullLiteral(final Expression type, final @Nullable Object value) {
        if (value == null) {
            throw new CriteriaException("Right operand of operator must be not null.");
        }
        final Expression resultExpression;
        if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = LiteralExpression.literal((TableField) type, value);
        } else {
            resultExpression = LiteralExpression.literal(type.paramMeta(), value);
        }
        return (ArmyExpression) resultExpression;
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
            resultExpression = LiteralExpression.literal((TableField) type, value);
        } else {
            resultExpression = LiteralExpression.literal(type.paramMeta(), value);
        }
        return (ArmyExpression) resultExpression;
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
     * @see Update._BatchSetClause
     */
    public static NamedParam nullableNamedParam(ParamMeta paramMeta, String name) {
        return NamedParamImpl.nullable(name, paramMeta);
    }

    /**
     * <p>
     * Create nullable named parameter expression for batch update(or delete)
     * </p>
     *
     * @see Update._BatchSetClause
     */
    public static NamedParam nullableNamedParam(final DataField field) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (TableField) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return NamedParamImpl.nullable(field.fieldName(), paramMeta);
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
    public static NamedParam namedParam(ParamMeta paramMeta, String name) {
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
    public static NamedParam namedParam(DataField field) {
        final ParamMeta paramMeta;
        if (field instanceof TableField) {
            paramMeta = (TableField) field;
        } else {
            paramMeta = field.paramMeta();
        }
        return NamedParamImpl.nonNull(field.fieldName(), paramMeta);
    }

    public static Expression namedParams(ParamMeta paramMeta, String name, int size) {
        return NamedCollectionParamExpression.named(name, paramMeta, size);
    }

    public static Expression namedParams(TableField field, int size) {
        return NamedCollectionParamExpression.named(field.fieldName(), field, size);
    }

    public static Expression literal(Object value) {
        Objects.requireNonNull(value);
        return LiteralExpression.literal(_MappingFactory.getDefault(value.getClass()), value);
    }

    public static Expression literal(ParamMeta paramMeta, Object value) {
        return LiteralExpression.literal(paramMeta, value);
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
    static _ItemPair _itemPair(final DataField field, final @Nullable AssignOperator operator
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

            if (!f.nullable() && (value == null || ((ArmyExpression) value).isNullableValue())) {
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
        final _ItemPair itemPair;
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


    /*################################## blow sql reference method ##################################*/

    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     */
    public static <T extends IDomain> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return QualifiedFieldImpl.reference(tableAlias, field);
    }

    public static DerivedField ref(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().ref(derivedTable, derivedFieldName);
    }

    public static DerivedField outerRef(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().outerRef(derivedTable, derivedFieldName);
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

    public static CteItem refCte(String cteName) {
        return CriteriaContextStack.peek().refCte(cteName);
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
    static final class NullWord extends NonOperationExpression {

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
        public String toString() {
            return _Constant.SPACE_NULL;
        }


    }// NullWord


    static abstract class ArmyItemPair implements _ItemPair {

        final SetRightItem right;

        private ArmyItemPair(SetRightItem right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #itemPair(DataField, Object)
     */
    static class FieldItemPair extends ArmyItemPair {

        final DataField field;

        private FieldItemPair(DataField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final _UpdateContext context) {
            final DataField field = this.field;
            //1. append left item
            context.appendSetLeftItem(field);
            //2. append operator
            if (this instanceof OperatorItemPair) {
                ((OperatorItemPair) this).operator.appendOperator(context.dialect().dialectMode(), field, context);
            } else {
                context.sqlBuilder()
                        .append(_Constant.SPACE_EQUAL);
            }
            //3. append right item
            ((_Expression) this.right).appendSql(context);
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

    static final class RowItemPair extends ArmyItemPair {

        final List<DataField> fieldList;

        private RowItemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = selectionCount(subQuery);
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
        public void appendItemPair(final _UpdateContext context) {
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
            context.dialect().rowSet((SubQuery) this.right, context);

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

    static final class StringTypeNull extends NonOperationExpression
            implements StrictParamValue, ValueExpression {

        static final StringTypeNull INSTANCE = new StringTypeNull();

        private StringTypeNull() {
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
                selectionCount = selectionCount((SubQuery) subStatement);
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
        public List<SelectItem> selectItemList() {
            final SubStatement subStatement = this.subStatement;
            final List<SelectItem> list;
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

    private static int selectionCount(final SubQuery query) {
        int count = 0;
        for (SelectItem selectItem : ((_Query) query).selectItemList()) {
            if (selectItem instanceof Selection) {
                count++;
            } else if (selectItem instanceof SelectionGroup) {
                count += ((SelectionGroup) selectItem).selectionList().size();
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }
        }
        return count;
    }


}
