package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.dialect.VarExpression;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;

final class ArmyVarExpression extends OperationExpression.OperationDefiniteExpression implements VarExpression {

    static SimpleExpression assignmentVar(final String varName, final SQLs.SymbolColonEqual colonEqual,
                                          final @Nullable Object value, final CriteriaContext context) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer("Initial value of User-Defined Variables must non-null");
        } else if (colonEqual != SQLs.COLON_EQUAL) {
            throw CriteriaUtils.unknownWords(colonEqual);
        } else if (!_StringUtils.hasText(varName)) {
            throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
        }

        TypeMeta type;
        if (value instanceof Expression) {
            type = ((Expression) value).typeMeta();
        } else {
            type = _MappingFactory.getDefaultIfMatch(value.getClass());
            if (type == null) {
                throw ContextStack.clearStackAnd(_Exceptions::notFoundMappingType, value);
            }
        }

        if (!(type instanceof MappingType)) {    // couldn't be field ,because filed codec
            type = type.mappingType();
        }

        final VarExpression varExp;
        varExp = new ArmyVarExpression(varName, (MappingType) type);
        context.registerVar(varExp);


        final SimpleExpression result;
        if (value instanceof Expression) {
            result = varExp.assignment((Expression) value);
        } else {
            result = varExp.assignment(SQLs.literal((MappingType) type, value));
        }
        return result;
    }

    private final String name;

    private final MappingType type;


    private ArmyVarExpression(String name, MappingType type) {   // couldn't be field ,because filed codec
        this.name = name;
        this.type = type;
    }

    @Override
    public TypeMeta typeMeta() {
        return this.type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public SimpleExpression increment() {
        return new VarOperationExpression(this, AssignOperator.PLUS_EQUAL, SQLs.LITERAL_1);
    }

    @Override
    public SimpleExpression decrement() {
        return new VarOperationExpression(this, AssignOperator.MINUS_EQUAL, SQLs.LITERAL_1);
    }

    @Override
    public SimpleExpression assignment(Expression value) {
        return new VarOperationExpression(this, null, value);
    }

    @Override
    public SimpleExpression plusEqual(Expression value) {
        return new VarOperationExpression(this, AssignOperator.PLUS_EQUAL, value);
    }

    @Override
    public SimpleExpression minusEqual(Expression value) {
        return new VarOperationExpression(this, AssignOperator.MINUS_EQUAL, value);
    }

    @Override
    public SimpleExpression timesEqual(Expression value) {
        return new VarOperationExpression(this, AssignOperator.TIMES_EQUAL, value);
    }

    @Override
    public SimpleExpression divideEqual(Expression value) {
        return new VarOperationExpression(this, AssignOperator.DIVIDE_EQUAL, value);
    }

    @Override
    public SimpleExpression modeEqual(Expression value) {
        return new VarOperationExpression(this, AssignOperator.MODE_EQUAL, value);
    }

    @Override
    public <T> SimpleExpression assignment(BiFunction<SimpleExpression, T, Expression> funcRef, T value) {
        return new VarOperationExpression(this, null, funcRef.apply(this, value));
    }

    @Override
    public <T> SimpleExpression plusEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value) {
        return new VarOperationExpression(this, AssignOperator.PLUS_EQUAL, funcRef.apply(this, value));
    }

    @Override
    public <T> SimpleExpression minusEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value) {
        return new VarOperationExpression(this, AssignOperator.MINUS_EQUAL, funcRef.apply(this, value));
    }

    @Override
    public <T> SimpleExpression timesEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value) {
        return new VarOperationExpression(this, AssignOperator.TIMES_EQUAL, funcRef.apply(this, value));
    }

    @Override
    public <T> SimpleExpression divideEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value) {
        return new VarOperationExpression(this, AssignOperator.DIVIDE_EQUAL, funcRef.apply(this, value));
    }

    @Override
    public <T> SimpleExpression modeEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value) {
        return new VarOperationExpression(this, AssignOperator.MODE_EQUAL, funcRef.apply(this, value));
    }

    @Override
    public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
        if (context.database() != Database.MySQL) {
            throw _Exceptions.dontSupportVariableExpression(context.database());
        }
        sqlBuilder.append(" @");
        context.identifier(this.name, sqlBuilder);
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ArmyVarExpression) {
            final ArmyVarExpression o = (ArmyVarExpression) obj;
            match = o.name.equals(this.name) && o.type == this.type;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return _StringUtils.builder(2 + this.name.length())
                .append(" @")
                .append(this.name)
                .toString();
    }

    private static final class VarOperationExpression extends OperationExpression.OperationSimpleExpression {

        private final ArmyVarExpression varExp;

        private final AssignOperator operator;

        private final ArmyExpression right;

        private VarOperationExpression(ArmyVarExpression varExp, @Nullable AssignOperator operator, @Nullable Expression right) {
            if (right == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.varExp = varExp;
            this.operator = operator;
            this.right = (ArmyExpression) right;
        }

        @Override
        public MappingType typeMeta() {   // couldn't be field ,because filed codec
            TypeMeta typeMeta;
            if (this.operator == null) {
                typeMeta = this.right.typeMeta();
            } else {
                typeMeta = this.varExp.type;
            }
            if (!(typeMeta instanceof MappingType)) {
                typeMeta = typeMeta.mappingType();
            }
            return (MappingType) typeMeta;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN); // outer left paren

            final ArmyVarExpression varExp = this.varExp;

            varExp.appendSql(sqlBuilder, context);
            sqlBuilder.append(" :=");

            final AssignOperator operator = this.operator;
            if (operator != null) {
                varExp.appendSql(sqlBuilder, context);
                operator.appendAppropriateExpressionOperator(sqlBuilder);
            }

            this.right.appendSql(sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN); // outer right paren

        } // VarOperationExpression


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE_LEFT_PAREN) // outer left paren
                    .append(this.varExp)
                    .append(" :=");

            final AssignOperator operator = this.operator;
            if (operator != null) {
                builder.append(this.varExp);
                operator.appendAppropriateExpressionOperator(builder);
            }

            builder.append(this.right);
            return builder.append(_Constant.SPACE_RIGHT_PAREN) // outer right paren
                    .toString();
        }


    } // VarOperationExpression


}
