package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.dialect._Constant;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping._NullType;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SingleParam;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * This class is util class used to create standard sql statement.
 * </p>
 */
@SuppressWarnings({"unused"})
public abstract class SQLs extends SQLsSyntax {

    /**
     * private constructor
     */
    private SQLs() {
    }

    static final Function<TypeInfer, TypeInfer> _IDENTITY = SQLs::_identity;

    static final Function<Insert, Insert> _INSERT_IDENTITY = SQLs::_identity;
    static final Function<Select, Select> _SELECT_IDENTITY = SQLs::_identity;

    static final Function<Update, Update> _UPDATE_IDENTITY = SQLs::_identity;
    static final Function<Delete, Delete> _DELETE_IDENTITY = SQLs::_identity;

    static final Function<SubQuery, SubQuery> _SUB_QUERY_IDENTITY = SQLs::_identity;

    public static StandardInsert._PrimaryOptionSpec<Insert> singleInsert() {
        return StandardInserts.primaryInsert(SQLs._INSERT_IDENTITY);
    }

    public static StandardUpdate._DomainUpdateClause domainUpdate() {
        return StandardUpdates.simpleDomain();
    }


    public static StandardUpdate._SingleUpdateClause<Update> singleUpdate() {
        return StandardUpdates.simpleSingle(SQLs._UPDATE_IDENTITY);
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     */
    public static StandardUpdate._BatchDomainUpdateClause batchDomainUpdate() {
        return StandardUpdates.batchDomain();
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     */
    public static StandardUpdate._BatchSingleUpdateClause batchSingleUpdate() {
        return StandardUpdates.batchSingle();
    }


    public static StandardDelete._StandardDeleteClause<Delete> singleDelete() {
        return StandardDeletes.singleDelete(SQLs._DELETE_IDENTITY);
    }

    public static StandardDelete._DomainDeleteClause domainDelete() {
        return StandardDeletes.domainDelete();
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     */
    public static StandardDelete._BatchDeleteClause<Delete> batchSingleDelete() {
        return StandardDeletes.batchSingleDelete(SQLs._DELETE_IDENTITY);
    }

    public static StandardDelete._BatchDomainDeleteClause batchDomainDelete() {
        return StandardDeletes.batchDomainDelete();
    }


    public static StandardQuery._SelectSpec<Select> query() {
        return StandardQueries.primaryQuery(ContextStack.peekIfBracket(), SQLs._SELECT_IDENTITY);
    }

    public static StandardQuery._SelectSpec<SubQuery> subQuery() {
        return StandardQueries.subQuery(ContextStack.peek(), SQLs._SUB_QUERY_IDENTITY);
    }


    public static StandardQuery._SelectSpec<Expression> scalarSubQuery() {
        return StandardQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }


    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     *
     * @param value {@link Expression} or parameter.
     * @see #plusEqual(DataField, Expression)
     */
    static SQLs.ArmyItemPair _itemPair(final @Nullable DataField field, final @Nullable AssignOperator operator,
                                       final @Nullable Expression value) {
        if (field == null || value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        //TODO right operand non-null validate
        final SQLs.ArmyItemPair itemPair;
        if (operator == null) {
            itemPair = new SQLs.FieldItemPair(field, (ArmyExpression) value);
        } else {
            itemPair = new SQLs.OperatorItemPair(field, operator, (ArmyExpression) value);
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

    static ItemPair _itemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
        return new SQLs.RowItemPair(fieldList, subQuery);
    }


    /**
     * <p>
     * This method is similar to {@link Function#identity()}, except that use method reference.
     * </p>
     *
     * @see Function#identity()
     */
    static <T extends Item> T _identity(T t) {
        return t;
    }

    static Expression _asExp(_ItemExpression<?> expression) {
        return expression;
    }

    static <I extends Item> Function<_ItemExpression<I>, Expression> _getAsExpFunc() {
        return SQLs::_asExp;
    }

    @SuppressWarnings("unchecked")
    static <T1 extends TypeInfer, T2 extends TypeInfer> Function<T1, T2> _getIdentity() {
        return (Function<T1, T2>) _IDENTITY;
    }


    static <I extends Item> Function<TypeInfer, I> _toSelection(final Function<Selection, I> function) {
        return t -> {
            if (!(t instanceof Selection)) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            return function.apply((Selection) t);
        };
    }

    static <I extends Item> Function<TypeInfer, I> _ToExp(final Function<Expression, I> function) {
        return t -> {
            if (!(t instanceof Expression)) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            return function.apply((Expression) t);
        };
    }

    static <I extends Item> Function<TypeInfer, I> _ToPredicate(final Function<IPredicate, I> function) {
        return t -> {
            if (!(t instanceof IPredicate)) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            return function.apply((IPredicate) t);
        };
    }

    static SQLIdentifier _identifier(@Nullable String identifier) {
        if (identifier == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return new SQLIdentifierImpl(identifier);
    }



    /*-------------------below package method-------------------*/


    static abstract class ArmyItemPair implements _ItemPair {

        final RightOperand right;

        private ArmyItemPair(RightOperand right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #_itemPair(DataField, AssignOperator, Expression)
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
                        .appendOperator(context.parser().dialect(), field, context);
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
            context.parser().subQuery((SubQuery) this.right, context);

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

    private static final class NullParam extends NonOperationExpression
            implements SingleParam, SqlValueParam.SingleValue {

        private static final NullParam INSTANCE = new NullParam();

        private NullParam() {
        }

        @Override
        public TypeMeta typeMeta() {
            return _NullType.INSTANCE;
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


    @Deprecated
    static final class ExpressionPairImpl implements ExpressionPair {

        final Expression first;

        final Expression second;

        private ExpressionPairImpl(@Nullable Expression first, @Nullable Expression second) {
            assert first != null;
            assert second != null;
            this.first = first;
            this.second = second;
        }
    }//ExpressionPairImpl


    static final class CteImpl implements _Cte, TabularItem._DerivedTableSpec, DerivedTable {

        final String name;

        final List<String> columnNameList;

        final SubStatement subStatement;

        CteImpl(String name, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = Collections.emptyList();
            if (subStatement instanceof DerivedTable) {
                ((ArmyDerivedTable) subStatement).setColumnAliasList(this.columnNameList);
            }
            this.subStatement = subStatement;
        }


        CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = columnNameList;
            this.subStatement = subStatement;
            if (subStatement instanceof DerivedTable) {
                ((ArmyDerivedTable) subStatement).setColumnAliasList(columnNameList);
            }
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public List<String> columnAliasList() {
            return this.columnNameList;
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<Selection> selectionList() {
            final SubStatement subStatement = this.subStatement;
            final List<Selection> list;
            if (subStatement instanceof DerivedTable) {
                list = ((ArmyDerivedTable) subStatement).selectionList();
            } else if (subStatement instanceof _Statement._ReturningListSpec) {
                list = ((_Statement._ReturningListSpec) subStatement).returningList();
            } else {
                list = Collections.emptyList();
            }
            return list;
        }


        @Override
        public Selection selection(final String derivedAlias) {
            final SubStatement subStatement = this.subStatement;
            final Selection selection;
            if (subStatement instanceof DerivedTable) {
                selection = ((ArmyDerivedTable) subStatement).selection(derivedAlias);
            } else {
                selection = null;
            }
            return selection;
        }


    }//CteImpl


    static final class SQLIdentifierImpl implements SQLIdentifier {

        private final String identifier;

        private SQLIdentifierImpl(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String render() {
            return this.identifier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.identifier);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SQLIdentifierImpl) {
                match = ((SQLIdentifierImpl) obj).identifier.equals(this.identifier);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return this.identifier;
        }


    }//SQLIdentifierImpl


}
