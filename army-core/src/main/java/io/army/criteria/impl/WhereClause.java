package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.function.TeExpression;
import io.army.function.TePredicate;
import io.army.lang.Nullable;
import io.army.stmt.Stmt;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * package class
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WhereClause<C, WR, WA, OR> extends OrderByClause<C, OR>
        implements Statement._WhereClause<C, WR, WA>
        , Statement._WhereAndClause<C, WA>
        , Update._UpdateWhereAndClause<C, WA>
        , _Statement._PredicateListSpec
        , Statement.StatementMockSpec {

    final CriteriaContext context;

    final C criteria;

    private List<_Predicate> predicateList;

    WhereClause(CriteriaContext context) {
        super(context);
        this.context = context;
        this.criteria = context.criteria();
    }


    @Override
    public final WR where(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        return (WR) this;
    }

    @Override
    public final WR where(BiConsumer<C, Consumer<IPredicate>> consumer) {
        consumer.accept(this.criteria, this::and);
        return (WR) this;
    }

    @Override
    public final WA where(IPredicate predicate) {
        return this.and(predicate);
    }

    @Override
    public final WA where(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA where(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }


    @Override
    public final WA where(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final <E> WA where(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <E> WA where(Function<E, IPredicate> expOperator, Function<C, E> function) {
        return this.and(expOperator.apply(function.apply(this.criteria)));
    }

    @Override
    public final WA where(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <T> WA where(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> valueOperator, @Nullable T operand) {
        if (operand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(valueOperator, operand));
    }

    @Override
    public final <T> WA where(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        if (operand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(valueOperator, operand));
    }

    @Override
    public final WA where(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        if (operand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(valueOperator, operand));
    }

    @Override
    public final <T> WA where(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T first
            , @Nullable T second) {
        if (first == null || second == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final <T> WA where(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        if ((first = firstSupplier.get()) == null || (second = secondSupplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final WA where(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
            , String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final WA where(TePredicate<TeExpression<Expression, String, Integer>, String, Integer> expOperator
            , TeExpression<Expression, String, Integer> namedOperator, @Nullable String paramName, int size) {
        if (paramName == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final WA where(BiFunction<TeExpression<DataField, String, Integer>, Integer, IPredicate> expOperator
            , TeExpression<DataField, String, Integer> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }


    @Override
    public final WA whereIf(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier);
    }

    @Override
    public final WA whereIf(Function<C, IPredicate> function) {
        return this.ifAnd(function);
    }


    @Override
    public final <E> WA whereIf(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.ifAnd(expOperator, supplier);
    }

    @Override
    public final <E> WA whereIf(Function<E, IPredicate> expOperator, Function<C, E> function) {
        return this.ifAnd(expOperator, function);
    }

    @Override
    public final <T> WA whereIf(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        return this.ifAnd(expOperator, operator, operand);
    }

    @Override
    public final <T> WA whereIf(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return this.ifAnd(expOperator, operator, supplier);
    }

    @Override
    public final WA whereIf(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.ifAnd(expOperator, operator, function, keyName);
    }

    @Override
    public final <T> WA whereIf(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        return this.ifAnd(expOperator, operator, first, second);
    }

    @Override
    public final <T> WA whereIf(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier
            , Supplier<T> secondSupplier) {
        return this.ifAnd(expOperator, operator, firstSupplier, secondSupplier);
    }

    @Override
    public final WA whereIf(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String firstKey, String secondKey) {
        return this.ifAnd(expOperator, operator, function, firstKey, secondKey);
    }

    @Override
    public final WA whereIf(TePredicate<TeExpression<Expression, String, Integer>, String, Integer> expOperator
            , TeExpression<Expression, String, Integer> namedOperator, @Nullable String paramName
            , @Nullable Integer size) {
        return this.ifAnd(expOperator, namedOperator, paramName, size);
    }

    @Override
    public final WA whereIf(BiFunction<TeExpression<DataField, String, Integer>, Integer, IPredicate> expOperator
            , TeExpression<DataField, String, Integer> namedOperator, @Nullable Integer size) {
        return this.ifAnd(expOperator, namedOperator, size);
    }


    @Override
    public final WA and(@Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.predicateList.add((OperationPredicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA and(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }


    @Override
    public final WA and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }


    @Override
    public final <E> WA and(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <E> WA and(Function<E, IPredicate> expOperator, Function<C, E> function) {
        return this.and(expOperator.apply(function.apply(this.criteria)));
    }

    @Override
    public final WA and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        if (operand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, operand));
    }

    @Override
    public final <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        if (operand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, operand));
    }

    @Override
    public final WA and(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        if (operand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, operand));
    }

    @Override
    public final <T> WA and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        if (first == null || second == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final <T> WA and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        if ((first = firstSupplier.get()) == null || (second = secondSupplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final WA and(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String firstKey, String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final WA and(TePredicate<TeExpression<Expression, String, Integer>, String, Integer> expOperator
            , TeExpression<Expression, String, Integer> namedOperator, @Nullable String paramName, int size) {
        if (paramName == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final WA and(BiFunction<TeExpression<DataField, String, Integer>, Integer, IPredicate> expOperator
            , TeExpression<DataField, String, Integer> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }

    @Override
    public final <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1
            , BiFunction<Expression, T, Expression> operator, @Nullable T operand1
            , BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable Number numberOperand) {
        if (operand1 == null || numberOperand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Expression expression;
        expression = expOperator1.apply(operator, operand1);
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator2.apply(expression, SQLs.literal(numberOperand)));
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }


    @Override
    public final <E> WA ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        final E expression;
        expression = supplier.get();
        if (expression != null) {
            this.and(expOperator.apply(expression));
        }
        return (WA) this;
    }

    @Override
    public final <E> WA ifAnd(Function<E, IPredicate> expOperator, Function<C, E> function) {
        final E expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.and(expOperator.apply(expression));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        if (first != null && second != null) {
            this.and(expOperator.apply(operator, first, second));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        if ((first = firstSupplier.get()) != null && (second = secondSupplier.get()) != null) {
            this.and(expOperator.apply(operator, first, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
            , String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
            this.and(expOperator.apply(operator, first, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(TePredicate<TeExpression<Expression, String, Integer>, String, Integer> expOperator
            , TeExpression<Expression, String, Integer> namedOperator, @Nullable String paramName
            , @Nullable Integer size) {
        if (paramName != null && size != null) {
            this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<TeExpression<DataField, String, Integer>, Integer, IPredicate> expOperator
            , TeExpression<DataField, String, Integer> namedOperator, @Nullable Integer size) {
        if (size != null) {
            this.and(expOperator.apply(namedOperator, size));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1
            , BiFunction<Expression, T, Expression> operator, @Nullable T operand1
            , BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable Number numberOperand) {
        if (operand1 != null && numberOperand != null) {
            final Expression expression;
            expression = expOperator1.apply(operator, operand1);
            if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.and(expOperator2.apply(expression, SQLs.literal(numberOperand)));
        }
        return (WA) this;
    }


    @Override
    public final List<_Predicate> predicateList() {
        final List<_Predicate> list = this.predicateList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
        final DialectParser parser;
        parser = _MockDialects.from(dialect);
        final Stmt stmt;
        stmt = this.parseStatement(parser, visible);
        return parser.printStmt(stmt, none);
    }

    @Override
    public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
        return this.parseStatement(_MockDialects.from(dialect), visible);
    }


    private Stmt parseStatement(final DialectParser parser, final Visible visible) {
        if (!(this instanceof PrimaryStatement)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final Stmt stmt;
        if (this instanceof Select) {
            stmt = parser.select((Select) this, visible);
        } else if (this instanceof Update) {
            stmt = parser.update((Update) this, visible);
        } else if (this instanceof Delete) {
            stmt = parser.delete((Delete) this, visible);
        } else if (this instanceof DialectStatement) {
            stmt = parser.dialectStmt((DialectStatement) this, visible);
        } else {
            throw new IllegalStateException("unknown statement");
        }
        return stmt;
    }


    final List<_Predicate> endWhereClause() {
        List<_Predicate> list = this.predicateList;
        if (list instanceof ArrayList) {
            list = _CollectionUtils.unmodifiableList(list);
            this.predicateList = list;
        } else if (list != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (this instanceof SimpleQueries) {
            list = Collections.emptyList();
            this.predicateList = list;
        } else {
            //dml statement must have where clause
            throw ContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
        }
        return list;
    }


}
