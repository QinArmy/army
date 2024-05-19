package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._RowSet;
import io.army.criteria.standard.SQLs;
import io.army.dialect._Constant;
import io.army.dialect._SetClauseContext;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class Armies {

    private Armies() {
    }

    static AssignmentItem _assignmentItem(final SqlField field, final @Nullable Object value) {
        final AssignmentItem item;
        if (value instanceof AssignmentItem) {
            item = (AssignmentItem) value;
        } else {
            item = SQLs.param(field, value);
        }
        return item;
    }

    static Expression _nonNullExp(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndCriteriaError("appropriate operator don't allow that operand is null");
        }
        if (value instanceof Expression) {
            return (Expression) value;
        }

        if (value instanceof RightOperand) {
            String m = String.format("appropriate operator don't allow that operand is %s",
                    value.getClass().getName());
            throw new CriteriaException(m);
        }
        return SQLs.paramValue(value);
    }

    public static Expression _nullableExp(final @Nullable Object value) {
        final Expression exp;
        switch (value) {
            case null -> exp = SQLs.NULL;
            case Expression expression -> exp = expression;
            case RightOperand ignored -> {
                String m = String.format("appropriate operator don't allow that operand is %s",
                        value.getClass().getName());
                throw new CriteriaException(m);
            }
            default -> exp = SQLs.paramValue(value);
        }
        return exp;
    }

    public static Expression _nonNullLiteral(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndCriteriaError("appropriate operator don't allow that operand is null");
        }
        if (value instanceof Expression) {
            return (Expression) value;
        }
        return SQLs.literalValue(value);
    }

    public static Expression _nullableLiteral(final @Nullable Object value) {
        final Expression expression;
        if (value == null) {
            expression = SQLs.NULL;
        } else if (value instanceof Expression) {
            expression = (Expression) value;
        } else {
            expression = SQLs.literalValue(value);
        }
        return expression;
    }

    /**
     * <p>
     * package method that is used by army developer.
     * *
     *
     * @param value {@link Expression} or parameter.
     * @see SQLs#plusEqual(SqlField, Expression)
     */
    public static ArmyItemPair _itemPair(final @Nullable SqlField field, final @Nullable AssignOperator operator,
                                         final @Nullable Expression value) {
        if (field == null || value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        //TODO right operand non-null validate
        final ArmyItemPair itemPair;
        if (operator == null) {
            itemPair = new FieldItemPair(field, (ArmyExpression) value);
        } else {
            itemPair = new OperatorItemPair(field, operator, (ArmyExpression) value);
        }
        return itemPair;
    }

    /**
     * <p>
     * package method that is used by army developer.
     */
    public static _ItemPair _itemExpPair(final SqlField field, @Nullable Expression value) {
        assert value != null;
        return _itemPair(field, null, value);
    }

    public static ItemPair _itemPair(List<? extends SqlField> fieldList, SubQuery subQuery) {
        return new RowItemPair(fieldList, subQuery);
    }

    /**
     * <p>
     * This method is similar to {@link Function#identity()}, except that use method reference.
     * *
     *
     * @see Function#identity()
     */
    public static <T extends Item> T identity(T t) {
        return t;
    }

    public static Item castCriteria(Item stmt) {
        throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
    }

    @Deprecated
    public static BatchUpdate _batchUpdateIdentity(UpdateStatement update) {
        return (BatchUpdate) update;
    }

    public static BatchDelete _batchDeleteIdentity(DeleteStatement delete) {
        return (BatchDelete) delete;
    }

    public static <I extends Item> Function<TypeInfer, I> _toSelection(final Function<Selection, I> function) {
        return t -> {
            if (!(t instanceof Selection)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return function.apply((Selection) t);
        };
    }

    public static <I extends Item> Function<TypeInfer, I> _toExp(final Function<Expression, I> function) {
        return t -> {
            if (!(t instanceof Expression)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return function.apply((Expression) t);
        };
    }


    public static abstract class ArmyItemPair implements _ItemPair {

        final RightOperand right;

        private ArmyItemPair(RightOperand right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #_itemPair(SqlField, AssignOperator, Expression)
     */
    static class FieldItemPair extends ArmyItemPair implements _ItemPair._FieldItemPair {

        final SqlField field;

        private FieldItemPair(SqlField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final StringBuilder sqlBuilder, final _SetClauseContext context) {
            final SqlField field = this.field;
            final _Expression right = (_Expression) this.right;

            if (right == SQLs.UPDATE_TIME_PARAM_PLACEHOLDER) {
                if (this instanceof OperatorItemPair) {
                    throw placeholderError("UPDATE_TIME_PARAM_PLACEHOLDER");
                }
                context.appendSetLeftItem(field, right); //  append left item
            } else if (right == SQLs.UPDATE_TIME_LITERAL_PLACEHOLDER) {
                if (this instanceof OperatorItemPair) {
                    throw placeholderError("UPDATE_TIME_LITERAL_PLACEHOLDER");
                }
                context.appendSetLeftItem(field, right); //  append left item
            } else {
                context.appendSetLeftItem(field, null); //  append left item
                //2. append operator
                if (this instanceof OperatorItemPair) {
                    ((OperatorItemPair) this).operator
                            .appendOperator(field, sqlBuilder, context);
                } else {
                    sqlBuilder.append(_Constant.SPACE_EQUAL);
                }
                //3. append right item
                ((_Expression) this.right).appendSql(sqlBuilder, context);
            }


        }


        @Override
        public final SqlField field() {
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

        private CriteriaException placeholderError(final String name) {
            String m = String.format("SQLs.%s don't support %s", name, ((OperatorItemPair) this).operator.name());
            throw new CriteriaException(m);
        }

    }//FieldItemPair

    private static final class OperatorItemPair extends FieldItemPair {

        final AssignOperator operator;

        private OperatorItemPair(SqlField field, AssignOperator operator, ArmyExpression value) {
            super(field, value);
            this.operator = operator;
        }


    }//OperatorItemPair

    public static final class RowItemPair extends ArmyItemPair implements _ItemPair._RowItemPair {

        final List<SqlField> fieldList;

        private RowItemPair(List<? extends SqlField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = ((_RowSet) subQuery).selectionSize();
            if (fieldList.size() != selectionCount) {
                String m = String.format("Row column count[%s] and selection count[%s] of SubQuery not match."
                        , fieldList.size(), selectionCount);
                throw new CriteriaException(m);
            }
            final List<SqlField> tempList = _Collections.arrayList(fieldList.size());
            for (SqlField field : fieldList) {
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
        public void appendItemPair(final StringBuilder sqlBuilder, final _SetClauseContext context) {
            final List<? extends SqlField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //1. append left paren
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendSetLeftItem(fieldList.get(i), null);
            }
            //3. append right paren
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            sqlBuilder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            context.appendSubQuery((SubQuery) this.right);

        }

        @Override
        public List<? extends SqlField> rowFieldList() {
            return this.fieldList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            //1. append left paren
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<? extends SqlField> fieldList = this.fieldList;
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


}
