package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect.*;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping.VoidType;
import io.army.meta.TypeMeta;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.*;

abstract class FunctionUtils {

    FunctionUtils() {
        throw new UnsupportedOperationException();
    }


    static SQLFunction._CaseFuncWhenClause caseFunction(final @Nullable Expression caseValue) {
        if (!(caseValue == null || caseValue instanceof OperationExpression)) {
            throw CriteriaUtils.funcArgError("CASE", caseValue);
        }
        return new CaseFunction((ArmyExpression) caseValue);
    }


    static SimpleExpression oneArgFunc(String name, Expression expr, TypeMeta returnType) {
        if (!(expr instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        return new OneArgFunction(name, (ArmyExpression) expr, returnType);
    }


    static SimpleExpression twoArgFunc(final String name, final Expression one, final Expression two, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgFunction(name, one, two, returnType);
    }

    static SimpleExpression threeArgFunc(final String name, final Expression one, final Expression two,
                                         final Expression three, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new ThreeArgFunction(name, one, two, three, returnType);
    }

    static SimpleExpression fourArgFunc(final String name, final Expression one, final Expression two,
                                        final Expression three, final Expression four, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        }
        return new FourArgFunction(name, one, two, three, four, returnType);
    }

    static SimpleExpression fiveArgFunc(final String name, final Expression one, final Expression two,
                                        final Expression three, final Expression four, final Expression five,
                                        final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        } else if (!(five instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, five);
        }
        return new FiveArgFunction(name, one, two, three, four, five, returnType);
    }

    static SimpleExpression sixArgFunc(final String name, final Expression one, final Expression two,
                                       final Expression three, final Expression four, final Expression five,
                                       final Expression six, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        } else if (!(five instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, five);
        } else if (!(six instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, six);
        }
        return new SixArgFunction(name, one, two, three, four, five, six, returnType);
    }

    static SimpleExpression sevenArgFunc(final String name, final Expression one, final Expression two,
                                         final Expression three, final Expression four, final Expression five,
                                         final Expression six, final Expression seven, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        } else if (!(five instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, five);
        } else if (!(six instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, six);
        } else if (!(seven instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, seven);
        }
        return new SevenArgFunction(name, one, two, three, four, five, six, seven, returnType);
    }

    static SimpleExpression oneNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                            final TypeMeta returnType) {
        assertNotation(name, null, validator, one);
        return oneArgFunc(name, one, returnType);
    }

    static SimpleExpression twoNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                            final Expression two, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        assertNotation(name, notation, validator, two);

        return twoArgFunc(name, one, two, returnType);
    }

    static SimpleExpression threeNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                              final Expression two, final Expression three, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        assertNotation(name, notation, validator, three);

        return threeArgFunc(name, one, two, three, returnType);
    }

    static SimpleExpression fourNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                             final Expression two, final Expression three, final Expression four,
                                             final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        assertNotation(name, notation, validator, four);

        return fourArgFunc(name, one, two, three, four, returnType);
    }

    static SimpleExpression fiveNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                             final Expression two, final Expression three, final Expression four,
                                             final Expression five, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        notation = assertNotation(name, notation, validator, four);

        assertNotation(name, notation, validator, five);

        return fiveArgFunc(name, one, two, three, four, five, returnType);
    }

    static SimpleExpression sixNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                            final Expression two, final Expression three, final Expression four,
                                            final Expression five, final Expression six, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        notation = assertNotation(name, notation, validator, four);

        notation = assertNotation(name, notation, validator, five);
        assertNotation(name, notation, validator, six);

        return sixArgFunc(name, one, two, three, four, five, six, returnType);
    }

    static SimpleExpression sevenNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                              final Expression two, final Expression three, final Expression four,
                                              final Expression five, final Expression six, final Expression seven,
                                              final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        notation = assertNotation(name, notation, validator, four);

        notation = assertNotation(name, notation, validator, five);
        notation = assertNotation(name, notation, validator, six);
        assertNotation(name, notation, validator, seven);

        return sevenArgFunc(name, one, two, three, four, five, six, seven, returnType);
    }


    static Expression namedNotation(final String name, final Expression argument) {
        if (!_DialectUtils.isSafeIdentifier(name)) {
            String m = String.format("named notation[%s] isn't simple identifier.", name);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        if (argument instanceof NamedNotation) {
            String m = String.format("argument[%s] of Named Notation[%s] couldn't be named Notation", name, argument);
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (argument instanceof SqlValueParam.MultiValue) {
            throw namedNotationIsMultiValue();
        }
        return new NamedNotation(name, (ArmyExpression) argument);
    }

    static Functions._ColumnFunction oneArgDerivedFunction(final String name, final Expression one,
                                                           final TypeMeta returnType) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgScalarTabularFunction(name, one, returnType);
    }

    static Functions._ColumnFunction twoArgDerivedFunction(final String name, final Expression one, final Expression two,
                                                           final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgScalarTabularFunction(name, one, two, returnType);
    }

    static Functions._ColumnFunction threeArgDerivedFunction(final String name, final Expression one, final Expression two,
                                                             final Expression three,
                                                             final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new ThreeArgScalarTabularFunction(name, one, two, three, returnType);
    }

    static Functions._ColumnFunction fourArgScalarFunction(final String name, final Expression one, final Expression two,
                                                           final Expression three, final Expression four,
                                                           final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        }
        return new FourArgScalarTabularFunction(name, one, two, three, four, returnType);
    }


    static SimplePredicate twoArgPredicateFunc(final String name, final Expression one, final Expression two) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new MultiArgFuncPredicate(name, null, twoExpList(name, one, two));
    }

    static SimplePredicate threeArgPredicateFunc(final String name, final Expression one, final Expression two
            , final Expression three) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new MultiArgFuncPredicate(name, null, threeExpList(name, one, two, three));
    }


    static SimpleExpression zeroArgFunc(String name, TypeMeta returnType) {
        return new ZeroArgFunction(name, returnType);
    }

    static SimpleExpression noParensFunc(String name, TypeMeta returnType) {
        return new NoParensFunction(name, returnType);
    }

    static SimpleExpression oneOrMultiArgFunc(String name, Expression exp, TypeMeta returnType) {
        if (!(exp instanceof FunctionArg)) {
            throw CriteriaUtils.funcArgError(name, exp);
        }
        return new OneArgFunction(name, (ArmyExpression) exp, returnType);
    }

    static SimpleExpression twoOrMultiArgFunc(final String name, final Expression one, final Expression two,
                                              TypeMeta returnType) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        final List<ArmyExpression> argList;
        argList = Arrays.asList((ArmyExpression) one, (ArmyExpression) two);
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static SimpleExpression oneAndMultiArgFunc(final String name, final Expression exp, final List<Expression> expList,
                                               final TypeMeta returnType) {
        if (exp instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, exp);
        }
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList = new ArrayList<>(1 + size);
        argList.add((ArmyExpression) exp);
        for (Expression e : expList) {
            argList.add((ArmyExpression) e);
        }
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static SimpleExpression twoAndMultiArgFunc(final String name, final Expression exp1, Expression exp2,
                                               final List<Expression> expList, final TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, null, twoAndMultiExpList(name, exp1, exp2, expList), returnType);
    }

    static SimpleExpression multiArgFunc(String name, List<Expression> argList, TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, null, expList(name, argList), returnType);
    }

    static SimpleExpression oneAndRestFunc(String name, TypeMeta returnType, Expression first, Expression... rest) {
        if (first instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final SimpleExpression func;
        if (rest.length == 0) {
            func = new OneArgFunction(name, (ArmyExpression) first, returnType);
        } else {
            final List<ArmyExpression> argList = new ArrayList<>(1 + rest.length);
            argList.add((ArmyExpression) first);
            addRestExp(argList, rest);
            func = new MultiArgFunctionExpression(name, null, argList, returnType);
        }
        return func;
    }


    static SimpleExpression twoAndMaxRestForSingleExpFunc(String name, TypeMeta returnType
            , Expression one, Expression two, final int maxRest, Expression... rest) {
        assert maxRest > 0;
        if (rest.length > maxRest) {
            String m = String.format("function[%s] at most take %s argument", name, maxRest);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
        final List<ArmyExpression> argList;
        argList = new ArrayList<>(2 + rest.length);
        appendTwoSingleExp(argList, name, one, two);
        for (Expression arg : rest) {
            if (arg instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.funcArgError(name, arg);
            }
            argList.add((ArmyExpression) arg);
        }
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }


    static SimpleExpression multiArgFunc(final String name, final TypeMeta returnType, final Expression firstArg,
                                         final Expression... exps) {
        final List<ArmyExpression> argList = new ArrayList<>(1 + exps.length);
        argList.add((ArmyExpression) firstArg);
        for (Expression exp : exps) {
            if (exp instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            argList.add((ArmyExpression) exp);
        }
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static SimpleExpression multiNotationFunc(final String name, final TypeMeta returnType, final Predicate<String> validator,
                                              final Expression firstArg, final Expression... restExp) {
        String notation = null;
        if (firstArg instanceof NamedNotation) {
            notation = ((NamedNotation) firstArg).name;
            if (validator.test(notation)) {
                throw errorNamedNotation(name, notation);
            }
        }
        for (Expression exp : restExp) {
            if (exp instanceof NamedNotation) {
                notation = ((NamedNotation) exp).name;
                if (validator.test(notation)) {
                    throw errorNamedNotation(name, notation);
                }
            } else if (notation != null) {
                throw positionalNotationAfterNamedNotation(notation);
            }
        }
        return multiArgFunc(name, returnType, firstArg, restExp);
    }


    static SimpleExpression safeMultiArgFunc(String name, List<ArmyExpression> argList, TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }


    static SimplePredicate noArgFuncPredicate(final String name) {
        return new NoArgFuncPredicate(name);
    }

    static SimplePredicate oneArgFuncPredicate(final String name, final Expression argument) {
        if (argument instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, argument);
        }
        return new OneArgFuncPredicate(name, (ArmyExpression) argument);
    }

    static SimplePredicate multiArgFuncPredicate(String name, List<Expression> expList) {
        final int size = expList.size();
        final SimplePredicate function;
        switch (size) {
            case 0:
                throw CriteriaUtils.funcArgError(name, expList);
            case 1:
                function = new OneArgFuncPredicate(name, (ArmyExpression) expList.get(0));
                break;
            default: {
                final List<ArmyExpression> argList = new ArrayList<>(size);
                appendExpList(argList, expList);
                function = new MultiArgFuncPredicate(name, null, argList);
            }
        }
        return function;
    }


    static SimplePredicate twoAndMultiArgFuncPredicate(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList) {
        return new MultiArgFuncPredicate(name, null, twoAndMultiExpList(name, exp1, exp2, expList));
    }


    static SimplePredicate complexArgPredicate(final String name, List<?> argList) {
        return new ComplexArgFuncPredicate(name, argList);
    }

    static SimplePredicate oneAndRestFuncPredicate(String name, Expression first, Expression... rest) {
        if (first instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final SimplePredicate func;
        if (rest.length == 0) {
            func = new OneArgFuncPredicate(name, (ArmyExpression) first);
        } else {
            final List<ArmyExpression> argList = new ArrayList<>(1 + rest.length);
            argList.add((ArmyExpression) first);
            addRestExp(argList, rest);
            func = new MultiArgFuncPredicate(name, null, argList);
        }
        return func;
    }

    static SimplePredicate complexArgPredicateFrom(final String name, Object firstArg, @Nullable Object... args) {
        final List<Object> argList;
        if (args == null) {
            argList = Collections.singletonList(firstArg);
        } else {
            argList = new ArrayList<>(args.length + 1);
            argList.add(firstArg);
            for (Object arg : args) {
                if (arg != null) {
                    argList.add(arg);
                }
            }
        }
        return new ComplexArgFuncPredicate(name, argList);
    }


    static SimpleExpression complexArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new ComplexArgFuncExpression(name, argList, returnType);
    }

    static SimpleExpression complexArgFunc(String name, TypeMeta returnType, Object... args) {
        final List<Object> argList = new ArrayList<>(args.length);
        Collections.addAll(argList, args);
        return new ComplexArgFuncExpression(name, argList, returnType);
    }

    static NamedExpression namedComplexArgFunc(String name, List<?> argList, TypeMeta returnType, String expAlias) {
        return new NamedComplexArgFunc(name, argList, returnType, expAlias);
    }

    static Functions._TabularWithOrdinalityFunction oneArgTabularFunc(final String name, final Expression one,
                                                                      final List<Selection> funcFieldList) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgTabularFunction(name, one, funcFieldList);
    }


    static SimpleExpression jsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
        return new JsonObjectFunc(name, expMap, returnType);
    }


    static void appendArguments(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final _SqlContext context) {

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

        if (option != null) {
            sqlBuilder.append(option.spaceRender());
        }

        final int argSize = argList.size();
        assert argSize > 0;

        for (int i = 0; i < argSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }

            argList.get(i).appendSql(context);
        }//for


    }

    static void argumentsToString(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final StringBuilder builder) {

        if (option != null) {
            builder.append(_Constant.SPACE)
                    .append(option.spaceRender());
        }

        final int argSize = argList.size();
        assert argSize > 0;
        for (int i = 0; i < argSize; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(argList.get(i));

        }//for


    }


    static void appendComplexArg(final List<?> argumentList, final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        DialectParser parser = null;
        for (Object o : argumentList) {
            if (o instanceof Expression) {
                ((ArmyExpression) o).appendSql(context); // convert to ArmyExpression to avoid non-army expression
            } else if (o == Functions.FuncWord.LEFT_PAREN) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
            } else if (o instanceof SQLWords) {
                sqlBuilder.append(((SQLWords) o).spaceRender());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                sqlBuilder.append(_Constant.SPACE);
                if (parser == null) {
                    parser = context.parser();
                }
                parser.identifier(((SQLIdentifier) o).render(), sqlBuilder);
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

        }//for

    }

    static void complexArgToString(final List<?> argumentList, final StringBuilder builder) {
        for (Object o : argumentList) {
            if (o instanceof Expression || o instanceof Clause) {
                builder.append(o);
            } else if (o == Functions.FuncWord.LEFT_PAREN) {
                builder.append(((SQLWords) o).spaceRender());
            } else if (o instanceof SQLWords) {
                builder.append(_Constant.SPACE)
                        .append(((SQLWords) o).spaceRender());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                builder.append(((SQLIdentifier) o).render());
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

        }//for
    }


    static void addRestExp(List<ArmyExpression> expList, Expression... rest) {
        for (Expression exp : rest) {
            expList.add((ArmyExpression) exp);
        }
    }

    static List<Object> twoArgList(final String name, Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(one);
        argList.add(Functions.FuncWord.COMMA);
        argList.add(two);
        return argList;
    }

    static List<ArmyExpression> twoExpList(final String name, Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return Arrays.asList((ArmyExpression) one, (ArmyExpression) two);
    }

    static List<ArmyExpression> threeExpList(final String name, Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return Arrays.asList((ArmyExpression) one, (ArmyExpression) two, (ArmyExpression) three);
    }

    static void appendExpList(final List<ArmyExpression> argList, final List<Expression> expList) {
        for (Expression exp : expList) {
            argList.add((ArmyExpression) exp);
        }
    }

    /**
     * @see #twoAndMaxRestForSingleExpFunc(String, TypeMeta, Expression, Expression, int, Expression...)
     */
    static void appendTwoSingleExp(List<ArmyExpression> argList, String name
            , Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) two);
    }

    static void appendThreeSingleExp(List<ArmyExpression> argList, String name
            , Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) two);
        argList.add((ArmyExpression) three);
    }


    static List<ArmyExpression> twoAndMultiExpList(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList) {
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList;
        argList = twoExpList(name, exp1, exp2);
        for (Expression e : expList) {
            argList.add((ArmyExpression) e);
        }
        return argList;
    }

    static List<ArmyExpression> expList(final String name, final List<Expression> expList) {
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList = new ArrayList<>(expList.size());
        for (Expression exp : expList) {
            argList.add((ArmyExpression) exp);
        }
        return argList;
    }


    static List<Object> threeArgList(final String name, Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        final List<Object> argList = new ArrayList<>(5);

        argList.add(one);
        argList.add(Functions.FuncWord.COMMA);
        argList.add(two);
        argList.add(Functions.FuncWord.COMMA);

        argList.add(three);
        return argList;
    }

    static ArmyExpression oneArgVoidFunc(final String name, final Expression arg) {
        if (!Functions.FUN_NAME_PATTER.matcher(name).matches()) {
            throw Functions._customFuncNameError(name);
        }
        return new MultiArgVoidFunction(name, Collections.singletonList(arg));
    }

    static ArmyExpression multiArgVoidFunc(final String name, final List<? extends Expression> argList) {
        if (!Functions.FUN_NAME_PATTER.matcher(name).matches()) {
            throw Functions._customFuncNameError(name);
        } else if (argList.size() == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        return new MultiArgVoidFunction(name, _CollectionUtils.unmodifiableList(argList));
    }


    static void appendMultiArgFunc(final String name, final List<? extends Expression> argList
            , final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE)
                .append(name)
                .append(_Constant.LEFT_PAREN);
        final int size = argList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.COMMA);
            }
            ((ArmyExpression) argList.get(i)).appendSql(context);
        }

        sqlBuilder.append(_Constant.RIGHT_PAREN);
    }

    /*-------------------below private method -------------------*/

    /**
     * @see #sixNotationFunc(String, Predicate, Expression, Expression, Expression, Expression, Expression, Expression, TypeMeta)
     */
    @Nullable
    private static String assertNotation(final String funcName, @Nullable String notation, Predicate<String> validator,
                                         final Expression argument) {
        if (argument instanceof NamedNotation) {
            notation = ((NamedNotation) argument).name;
            if (validator.test(notation)) {
                throw errorNamedNotation(funcName, notation);
            }
        } else if (notation != null) {
            throw positionalNotationAfterNamedNotation(notation);
        }
        return notation;
    }

    /**
     * @see MultiFieldTabularFunction#refSelection(String)
     * @see ColumnTabularFunctionWrapper#refSelection(String)
     */
    private static Map<String, Selection> createSelectionMapFrom(final CriteriaContext context,
                                                                 final List<? extends Selection> selectionList) {
        final Map<String, Selection> map = _CollectionUtils.hashMap((int) (selectionList.size() / 0.75F));
        for (Selection s : selectionList) {
            if (map.putIfAbsent(s.selectionName(), s) != null) {
                String m = String.format("Tabular %s %s name[%s] duplication.", SQLFunction.class.getName(),
                        Selection.class.getName(), s.selectionName());
                throw ContextStack.criteriaError(context, m);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    private static CriteriaException namedNotationIsMultiValue() {
        throw ContextStack.clearStackAndCriteriaError("Named Notation couldn't be %s multi-value parameter/literal.");
    }

    private static CriteriaException errorNamedNotation(String funcName, String notation) {
        String m = String.format("Named Notation[%s] isn't %s()'s Named Notation", notation, funcName);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    private static CriteriaException positionalNotationAfterNamedNotation(String notation) {
        String m = String.format("Couldn't present Positional Notation after Named Notation[%s]", notation);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    interface FunctionSpec extends _SelfDescribed, TypeInfer {

    }

    interface NoArgFunction {

    }


    enum NullTreatment implements SQLWords {

        RESPECT_NULLS(" RESPECT NULLS"),
        IGNORE_NULLS(" IGNORE NULLS");

        final String spaceWords;

        NullTreatment(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }//NullTreatment

    enum FromFirstLast implements SQLWords {

        FROM_FIRST(" FROM FIRST"),
        FROM_LAST(" FROM LAST");

        final String spaceWords;

        FromFirstLast(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//FromFirstLast


    static abstract class WindowFunction extends OperationExpression.SqlFunctionExpression
            implements Window._OverWindowClause,
            CriteriaContextSpec {

        final CriteriaContext context;

        final String name;

        final TypeMeta returnType;

        private String existingWindowName;

        private _Window anonymousWindow;

        WindowFunction(String name, TypeMeta returnType) {
            this.context = ContextStack.peek();
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeMeta.Delay && ((TypeMeta.Delay) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final Expression over(final @Nullable String windowName) {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (windowName == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.context.onRefWindow(windowName);
            this.existingWindowName = windowName;
            return this;
        }

        @Override
        public final Expression over() {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = GlobalWindow.INSTANCE;
            return this;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            //1. function
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);
            if (this instanceof NoArgFunction) {
                sqlBuilder.append(_Constant.RIGHT_PAREN);
            } else {
                this.appendArguments(context);
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (this instanceof SQLFunction._OuterClauseBeforeOver) {
                this.appendOuterClause(context);
            }

            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            final DialectParser parser;
            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof SQLFunction.AggregateFunction)) {
                    throw _Exceptions.castCriteriaApi();
                }
            } else if (existingWindowName != null && anonymousWindow != null) {
                throw _Exceptions.castCriteriaApi();
            } else if (this.isDontSupportWindow((parser = context.parser()).dialect())) {
                String m = String.format("%s don't support %s window function.", parser.dialect(), this.name);
                throw new CriteriaException(m);
            } else {
                sqlBuilder.append(_Constant.SPACE_OVER);
                if (existingWindowName != null) {
                    sqlBuilder.append(_Constant.SPACE);
                    context.parser().identifier(existingWindowName, sqlBuilder);
                } else if (anonymousWindow == GlobalWindow.INSTANCE) {
                    sqlBuilder.append(_Constant.PARENS);
                } else {
                    anonymousWindow.appendSql(context);
                }
            }

        }

        @Override
        public final String toString() {
            //1. function
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            if (!(this instanceof NoArgFunction)) {
                this.argumentToString(sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            if (this instanceof SQLFunction._OuterClauseBeforeOver) {
                this.outerClauseToString(sqlBuilder);
            }
            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof SQLFunction.AggregateFunction)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
            } else if (existingWindowName != null && anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else {
                //2. OVER clause
                sqlBuilder.append(_Constant.SPACE_OVER);
                if (existingWindowName != null) {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(existingWindowName);
                } else if (anonymousWindow == GlobalWindow.INSTANCE) {
                    sqlBuilder.append(_Constant.PARENS);
                } else {
                    sqlBuilder.append(anonymousWindow);
                }
            }
            return sqlBuilder.toString();
        }

        abstract void appendArguments(_SqlContext context);

        abstract void argumentToString(StringBuilder builder);

        abstract boolean isDontSupportWindow(Dialect dialect);

        void appendOuterClause(_SqlContext context) {
            throw new UnsupportedOperationException();
        }

        void outerClauseToString(StringBuilder builder) {
            throw new UnsupportedOperationException();
        }

        final Expression endWindow(final ArmyWindow anonymousWindow) {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = anonymousWindow.endWindowClause();
            return this;
        }

    }//AggregateOverClause


    static final class NamedNotation extends NonOperationExpression.NonOperationFunction {

        final String name;

        private final ArmyExpression argument;

        /**
         * @see #namedNotation(String, Expression)
         */
        private NamedNotation(String name, ArmyExpression argument) {
            if (argument instanceof SqlValueParam.MultiValue) {
                throw namedNotationIsMultiValue();
            }
            this.name = name;
            this.argument = argument;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.argument.typeMeta();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Database database;
            database = context.parser().dialect().database();
            switch (database) {
                case PostgreSQL: {
                    context.sqlBuilder()
                            .append(_Constant.SPACE)
                            .append(this.name) // TODO validate whether import key word or not.
                            .append(" =>");
                    this.argument.appendSql(context);
                }
                break;
                case MySQL:
                case H2:
                default: {
                    String m = String.format("%s don't support named notation", database);
                    throw new CriteriaException(m);
                }

            }

        }


        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.argument);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedNotation) {
                final NamedNotation o = (NamedNotation) obj;
                match = o.name.equals(this.name)
                        && o.argument.equals(this.argument);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(" =>")
                    .append(this.argument)
                    .toString();
        }


    }//NamedNotation

    private static final class NoParensFunction extends OperationExpression.SqlFunctionExpression {

        private final String name;

        private final TypeMeta returnType;

        /**
         * @see #noParensFunc(String, TypeMeta)
         */
        private NoParensFunction(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeInfer.DelayTypeInfer
                    && ((DelayTypeInfer) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name);
            // no parens
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NoParensFunction) {
                final NoParensFunction o = (NoParensFunction) obj;
                match = o.name.equals(this.name)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .toString();
        }


    }//NoParensFunction


    private static final class ZeroArgFunction extends OperationExpression.SqlFunctionExpression {

        private final String name;

        private final TypeMeta returnType;

        /**
         * @see #zeroArgFunc(String, TypeMeta)
         */
        private ZeroArgFunction(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.PARENS);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ZeroArgFunction) {
                final ZeroArgFunction o = (ZeroArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.PARENS)
                    .toString();
        }


    }//NoArgFuncExpression


    static abstract class FunctionExpression extends OperationExpression.SqlFunctionExpression {

        final String name;

        final TypeMeta returnType;

        FunctionExpression(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeMeta.Delay && ((TypeMeta.Delay) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            this.appendArg(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);
            this.argToString(builder);
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


        abstract void appendArg(_SqlContext context);

        abstract void argToString(StringBuilder builder);


    }//FunctionExpression

    private static final class OneArgFunction extends FunctionExpression {

        private final ArmyExpression argument;

        private OneArgFunction(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.argument, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof OneArgFunction) {
                final OneArgFunction o = (OneArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.argument.equals(this.argument)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            this.argument.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.argument);
        }


    }//OneArgFunction

    private static final class TwoArgFunction extends FunctionExpression {

        private final ArmyExpression one;

        private final ArmyExpression two;

        /**
         * @see #twoArgFunc(String, Expression, Expression, TypeMeta)
         */
        private TwoArgFunction(String name, Expression one, Expression two, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.one, this, two, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof TwoArgFunction) {
                final TwoArgFunction o = (TwoArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.one.equals(this.one)
                        && o.two.equals(this.two)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            this.one.appendSql(context);
            context.sqlBuilder()
                    .append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgFunction


    private static final class ThreeArgFunction extends FunctionExpression {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        /**
         * @see #threeArgFunc(String, Expression, Expression, Expression, TypeMeta)
         */
        private ThreeArgFunction(String name, Expression one, Expression two, Expression three,
                                 TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.one, this, two, this.three, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ThreeArgFunction) {
                final ThreeArgFunction o = (ThreeArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.one.equals(this.one)
                        && o.two.equals(this.two)
                        && o.three.equals(this.three)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three);
        }


    }//ThreeArgFunction


    private static final class FourArgFunction extends FunctionExpression {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private final ArmyExpression four;

        /**
         * @see #fourArgFunc(String, Expression, Expression, Expression, Expression, TypeMeta)
         */
        private FourArgFunction(String name, Expression one, Expression two, Expression three, Expression four,
                                TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
            this.four = (ArmyExpression) four;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.one, this, two, this.three, this.four, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof FourArgFunction) {
                final FourArgFunction o = (FourArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.one.equals(this.one)
                        && o.two.equals(this.two)
                        && o.three.equals(this.three)
                        && o.four.equals(this.four)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four);
        }


    }//FourArgFunction

    private static final class FiveArgFunction extends FunctionExpression {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private final ArmyExpression four;

        private final ArmyExpression five;


        /**
         * @see #fiveArgFunc(String, Expression, Expression, Expression, Expression, Expression, TypeMeta)
         */
        private FiveArgFunction(String name, Expression one, Expression two, Expression three, Expression four,
                                Expression five, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
            this.four = (ArmyExpression) four;
            this.five = (ArmyExpression) five;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.one, this, two, this.three, this.four, this.five, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof FiveArgFunction) {
                final FiveArgFunction o = (FiveArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.one.equals(this.one)
                        && o.two.equals(this.two)
                        && o.three.equals(this.three)
                        && o.four.equals(this.four)
                        && o.five.equals(this.five)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.five.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.five);
        }


    }//FiveArgFunction


    private static final class SixArgFunction extends FunctionExpression {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private final ArmyExpression four;

        private final ArmyExpression five;

        private final ArmyExpression six;


        /**
         * @see #sixArgFunc(String, Expression, Expression, Expression, Expression, Expression, Expression, TypeMeta)
         */
        private SixArgFunction(String name, Expression one, Expression two, Expression three, Expression four,
                               Expression five, Expression six, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
            this.four = (ArmyExpression) four;

            this.five = (ArmyExpression) five;
            this.six = (ArmyExpression) six;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.one, this, two, this.three, this.four, this.five, this.six, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SixArgFunction) {
                final SixArgFunction o = (SixArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.one.equals(this.one)
                        && o.two.equals(this.two)
                        && o.three.equals(this.three)
                        && o.four.equals(this.four)
                        && o.five.equals(this.five)
                        && o.six.equals(this.six)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.five.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.six.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.five)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.six);
        }


    }//SixArgFunction


    private static final class SevenArgFunction extends FunctionExpression {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private final ArmyExpression four;

        private final ArmyExpression five;

        private final ArmyExpression six;

        private final ArmyExpression seven;


        /**
         * @see #sevenArgFunc(String, Expression, Expression, Expression, Expression, Expression, Expression, Expression, TypeMeta)
         */
        private SevenArgFunction(String name, Expression one, Expression two, Expression three, Expression four,
                                 Expression five, Expression six, Expression seven, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
            this.four = (ArmyExpression) four;

            this.five = (ArmyExpression) five;
            this.six = (ArmyExpression) six;
            this.seven = (ArmyExpression) seven;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.one, this, two, this.three, this.four, this.five, this.six, this.seven,
                    this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SevenArgFunction) {
                final SevenArgFunction o = (SevenArgFunction) obj;
                match = o.name.equals(this.name)
                        && o.one.equals(this.one)
                        && o.two.equals(this.two)
                        && o.three.equals(this.three)
                        && o.four.equals(this.four)
                        && o.five.equals(this.five)
                        && o.six.equals(this.six)
                        && o.seven.equals(this.seven)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.five.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.six.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.seven.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.five)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.six)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.seven);
        }


    }//SevenArgFunction


    private static final class MultiArgFunctionExpression extends FunctionExpression {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private MultiArgFunctionExpression(String name, @Nullable final SQLWords option
                , List<ArmyExpression> argList, TypeMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }

        @Override
        void appendArg(final _SqlContext context) {
            FunctionUtils.appendArguments(this.option, this.argList, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            FunctionUtils.argumentsToString(this.option, this.argList, builder);
        }


    }//MultiArgFunctionExpression


    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link NoArgFuncPredicate}</li>
     *         <li>{@link OneArgFuncPredicate}</li>
     *         <li>{@link MultiArgFuncPredicate}</li>
     *     </ul>
     * </p>
     */
    private static abstract class FunctionPredicate extends OperationPredicate.SqlFunctionPredicate
            implements SQLFunction {


        FunctionPredicate(String name) {
            super(name);
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            if (this instanceof OneArgFuncPredicate) {
                ((OneArgFuncPredicate) this).argument.appendSql(context);
            } else if (this instanceof MultiArgFuncPredicate) {
                final MultiArgFuncPredicate p = (MultiArgFuncPredicate) this;
                FunctionUtils.appendArguments(p.option, p.argList, context);
            } else if (!(this instanceof NoArgFuncPredicate)) {
                //no bug,never here
                throw new IllegalStateException();
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public final String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            if (this instanceof OneArgFuncPredicate) {
                builder.append(((OneArgFuncPredicate) this).argument);
            } else if (this instanceof MultiArgFuncPredicate) {
                final MultiArgFuncPredicate p = (MultiArgFuncPredicate) this;
                FunctionUtils.argumentsToString(p.option, p.argList, builder);
            } else if (!(this instanceof NoArgFuncPredicate)) {
                //no bug,never here
                throw new IllegalStateException();
            }
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }

    }//FunctionPredicate


    private static final class NoArgFuncPredicate extends FunctionPredicate implements NoArgFunction {

        private NoArgFuncPredicate(String name) {
            super(name);
        }


        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NoArgFuncPredicate) {
                match = ((NoArgFuncPredicate) obj).name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }


    }//NoArgFuncPredicate


    private static final class OneArgFuncPredicate extends FunctionPredicate {


        private final ArmyExpression argument;

        private OneArgFuncPredicate(String name, ArmyExpression argument) {
            super(name);
            this.argument = argument;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.argument);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof OneArgFuncPredicate) {
                final OneArgFuncPredicate o = (OneArgFuncPredicate) obj;
                match = o.name.equals(this.name)
                        && o.argument.equals(this.argument);
            } else {
                match = false;
            }
            return match;
        }


    }//OneArgFuncPredicate

    /**
     * @see #threeArgPredicateFunc(String, Expression, Expression, Expression)
     */
    private static final class MultiArgFuncPredicate extends FunctionPredicate {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        MultiArgFuncPredicate(String name, @Nullable SQLWords option, List<ArmyExpression> argList) {
            super(name);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.option, this.argList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof MultiArgFuncPredicate) {
                final MultiArgFuncPredicate o = (MultiArgFuncPredicate) obj;
                match = o.name.equals(this.name)
                        && Objects.equals(o.option, this.option)
                        && o.argList.equals(this.argList);
            } else {
                match = false;
            }
            return match;
        }


    }//MultiArgFuncPredicate


    private static final class MultiArgVoidFunction extends NonOperationExpression.NonOperationFunction {

        private final String name;

        private final List<? extends Expression> argList;

        private MultiArgVoidFunction(String name, List<? extends Expression> argList) {
            this.name = name;
            this.argList = argList;
        }


        @Override
        public TypeMeta typeMeta() {
            return VoidType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            FunctionUtils.appendMultiArgFunc(this.name, this.argList, context);
        }


    }//MultiArgVoidFunction


    /**
     * @see ComplexArgFuncExpression
     */
    private static final class ComplexArgFuncPredicate extends OperationPredicate.SqlFunctionPredicate {


        private final List<?> argumentList;

        /**
         * @see #complexArgPredicate(String, List)
         */
        private ComplexArgFuncPredicate(String name, List<?> argumentList) {
            super(name);
            this.argumentList = argumentList;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.appendComplexArg(this.argumentList, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.argumentList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ComplexArgFuncPredicate) {
                final ComplexArgFuncPredicate o = (ComplexArgFuncPredicate) obj;
                match = o.name.equals(this.name) && o.argumentList.equals(this.argumentList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.complexArgToString(this.argumentList, builder);
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ComplexArgFuncPredicate


    /**
     * @see ComplexArgFuncPredicate
     */
    private static class ComplexArgFuncExpression extends OperationExpression.SqlFunctionExpression {

        private final String name;
        private final List<?> argList;

        private final TypeMeta returnType;

        /**
         * @see #complexArgFunc(String, TypeMeta, Object...)
         */
        private ComplexArgFuncExpression(String name, List<?> argList, TypeMeta returnType) {
            assert argList.size() > 0;
            this.name = name;
            this.argList = argList;
            this.returnType = returnType;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeMeta.Delay && ((TypeMeta.Delay) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.appendComplexArg(this.argList, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }


        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.complexArgToString(this.argList, builder);
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ComplexArgFuncExpression

    private static abstract class TabularSqlFunction implements _DerivedTable,
            Functions._TabularFunction, _SelfDescribed {

        static final String ORDINALITY = "ordinality";

        private static final String SPACE_WITH_ORDINALITY = " WITH ORDINALITY";

        final CriteriaContext outerContext;

        final String name;

        private TabularSqlFunction(String name) {
            this.name = name;
            this.outerContext = ContextStack.peek();
        }

        private TabularSqlFunction(ColumnTabularFunction columnFunction) {
            this.outerContext = columnFunction.outerContext;
            this.name = columnFunction.name;
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            if (context.isLowerFunctionName()) {
                sqlBuilder.append(this.name.toLowerCase(Locale.ROOT));
            } else {
                sqlBuilder.append(this.name);
            }
            sqlBuilder.append(_Constant.LEFT_PAREN);

            appendArg(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            if (this.isWithOrdinality()) {
                sqlBuilder.append(SPACE_WITH_ORDINALITY);
            }

        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            argToString(builder);
            builder.append(_Constant.SPACE_RIGHT_PAREN);
            if (this.isWithOrdinality()) {
                builder.append(SPACE_WITH_ORDINALITY);
            }
            return builder.toString();
        }

        abstract void appendArg(_SqlContext context);

        abstract void argToString(StringBuilder builder);

        private boolean isWithOrdinality() {
            final boolean withOrdinality;
            if (this instanceof MultiFieldTabularFunction) {
                final Boolean v = ((MultiFieldTabularFunction) this).ordinality;
                withOrdinality = v != null && v;
            } else if (this instanceof ColumnTabularFunctionWrapper) {
                withOrdinality = ((ColumnTabularFunctionWrapper) this).withOrdinality;
            } else if (this instanceof ColumnTabularFunction) {
                withOrdinality = false;
            } else {
                // no bug, never here
                throw new IllegalArgumentException();
            }
            return withOrdinality;
        }


    }//TabularSqlFunction


    private static final class ColumnTabularFunctionWrapper extends TabularSqlFunction {

        private final ColumnTabularFunction columnFunction;

        private final List<Selection> funcFieldList;

        private final boolean withOrdinality;

        private Map<String, Selection> selectionMap;

        private ColumnTabularFunctionWrapper(ColumnTabularFunction columnFunction, boolean withOrdinality) {
            super(columnFunction);
            this.columnFunction = columnFunction;
            final List<Selection> fieldList = _CollectionUtils.arrayList(2);
            fieldList.add(ArmySelections.forName(columnFunction.name.toLowerCase(Locale.ROOT), columnFunction.returnType));
            fieldList.add(ArmySelections.forName(ORDINALITY, LongType.INSTANCE));

            this.funcFieldList = _ArrayUtils.asUnmodifiableList(fieldList);
            this.withOrdinality = withOrdinality;
        }

        @Override
        void appendArg(_SqlContext context) {
            this.columnFunction.appendArg(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            this.columnFunction.argToString(builder);
        }

        @Override
        public Selection refSelection(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = createSelectionMapFrom(this.outerContext, this.funcFieldList);
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.funcFieldList;
        }


    }//ColumnTabularFunctionWrapper


    private static abstract class ColumnTabularFunction extends TabularSqlFunction
            implements Functions._ColumnWithOrdinalityFunction, _Selection {

        private final TypeMeta returnType;

        private String selectionName;

        private TypeMeta mapToType;

        private Boolean ordinality;

        private ColumnTabularFunction(String name, TypeMeta returnType) {
            super(name);
            this.returnType = returnType;
        }

        @Override
        public final Selection as(final @Nullable String selectionAlas) {
            if (this.ordinality != null || this.selectionName != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (selectionAlas == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            this.selectionName = selectionAlas;
            return this;
        }

        @Override
        public final String selectionName() {
            final String selectionName = this.selectionName;
            if (selectionName == null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            return selectionName;
        }


        @Override
        public final void appendSelectItem(final _SqlContext context) {
            final String selectionName = this.selectionName;
            if (this.ordinality != null || selectionName == null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(selectionName, sqlBuilder);

        }

        @Override
        public final Functions._TabularFunction withOrdinality() {
            if (this.ordinality != null || this.selectionName != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            this.ordinality = Boolean.TRUE;
            return new ColumnTabularFunctionWrapper(this, true);
        }

        @Override
        public final Functions._TabularFunction ifWithOrdinality(BooleanSupplier predicate) {
            if (this.ordinality != null || this.selectionName != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            final boolean withOrdinality;
            withOrdinality = predicate.getAsBoolean();
            this.ordinality = withOrdinality;
            return new ColumnTabularFunctionWrapper(this, withOrdinality);
        }

        @Override
        public final Selection refSelection(final String name) {
            final String selectionName = this.selectionName;
            if (this.ordinality != null || selectionName == null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            return selectionName.equals(name) ? this : null;
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.ordinality != null || this.selectionName == null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            return Collections.singletonList(this);
        }

        @Override
        public final TypeMeta typeMeta() {
            if (this.ordinality != null || this.selectionName == null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            TypeMeta type = this.mapToType;
            if (type == null) {
                type = this.returnType;
            }
            return type;
        }

        @Override
        public final SelectionSpec mapTo(final @Nullable TypeMeta mapType) {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (this.mapToType != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (mapType == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            this.mapToType = mapType;
            return this;
        }

        @Override
        public final TableField tableField() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            // always null
            return null;
        }

        @Override
        public final Expression underlyingExp() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            // always null
            return null;
        }


    }//ScalarTabularFunction


    private static final class OneArgScalarTabularFunction extends ColumnTabularFunction {

        private final ArmyExpression one;

        /**
         * @see #oneArgDerivedFunction(String, Expression, TypeMeta)
         */
        private OneArgScalarTabularFunction(String name, Expression one, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
        }


        @Override
        void appendArg(final _SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgScalarTabularFunction


    private static final class TwoArgScalarTabularFunction extends ColumnTabularFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private TwoArgScalarTabularFunction(String name, Expression one, Expression two, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }


        @Override
        void appendArg(final _SqlContext context) {
            this.one.appendSql(context);
            context.sqlBuilder().append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgScalarTabularFunction


    private static final class ThreeArgScalarTabularFunction extends ColumnTabularFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private ThreeArgScalarTabularFunction(String name, Expression one, Expression two, Expression three,
                                              TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
        }


        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three);
        }


    }//ThreeArgScalarTabularFunction

    private static final class FourArgScalarTabularFunction extends ColumnTabularFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private final ArmyExpression four;

        /**
         * @see #fourArgScalarFunction(String, Expression, Expression, Expression, Expression, TypeMeta)
         */
        private FourArgScalarTabularFunction(String name, Expression one, Expression two, Expression three, Expression four,
                                             TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
            this.four = (ArmyExpression) four;
        }


        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four);
        }


    }//FourArgScalarTabularFunction

    private static abstract class MultiFieldTabularFunction extends TabularSqlFunction
            implements Functions._TabularWithOrdinalityFunction {


        private final List<Selection> funcFieldList;

        private List<Selection> actualFuncFieldList;

        private Map<String, Selection> fieldMap;

        private Boolean ordinality;


        private MultiFieldTabularFunction(String name, List<Selection> funcFieldList) {
            super(name);
            assert funcFieldList.size() > 0;
            this.funcFieldList = Collections.unmodifiableList(funcFieldList);
            this.actualFuncFieldList = this.funcFieldList;
        }

        @Override
        public final Selection refSelection(final @Nullable String name) {
            if (this.ordinality == null) {
                this.ordinality = Boolean.FALSE;
            }
            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            Map<String, Selection> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = createSelectionMapFrom(this.outerContext, this.refAllSelection());// must invoke this.refAllSelection()
                this.fieldMap = fieldMap;
            }
            return fieldMap.get(name);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {

            final Boolean ordinality = this.ordinality;
            if (ordinality == null) {
                this.ordinality = Boolean.FALSE;
            }

            final List<Selection> actualFieldList = this.actualFuncFieldList;
            if (actualFieldList == this.funcFieldList) {
                assert ordinality == null || !ordinality;
            } else {
                final int actualFieldsSize;
                actualFieldsSize = actualFieldList.size();

                assert ordinality != null && ordinality;
                assert actualFieldsSize - this.funcFieldList.size() == 1;
                assert actualFieldList.get(actualFieldsSize - 1).selectionName().equals(ORDINALITY);
            }
            return actualFieldList;
        }

        @Override
        public final Functions._TabularFunction withOrdinality() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            final List<Selection> list = new ArrayList<>(this.funcFieldList.size() + 1);
            list.addAll(this.funcFieldList);
            list.add(ArmySelections.forName(ORDINALITY, LongType.INSTANCE));
            this.actualFuncFieldList = Collections.unmodifiableList(list);

            this.ordinality = Boolean.TRUE;
            return this;
        }

        @Override
        public final Functions._TabularFunction ifWithOrdinality(final BooleanSupplier predicate) {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (predicate.getAsBoolean()) {
                this.withOrdinality();
            } else {
                this.ordinality = Boolean.FALSE;
            }
            return this;
        }


    }//MultiFieldTabularFunction


    private static final class OneArgTabularFunction extends MultiFieldTabularFunction {

        private final FunctionArg one;

        /**
         * @see #oneArgTabularFunc(String, Expression, List)
         */
        private OneArgTabularFunction(String name, Expression one, List<Selection> funcFieldList) {
            super(name, funcFieldList);
            this.one = (FunctionArg) one;
        }


        @Override
        void appendArg(_SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgTabularFunction

    private static final class JsonObjectFunc extends OperationExpression.SqlFunctionExpression {

        private final String name;

        private final Map<String, Expression> expMap;

        private final TypeMeta returnType;

        private JsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
            assert expMap.size() > 0;
            this.name = name;
            this.expMap = new HashMap<>(expMap);
            this.returnType = returnType;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeMeta.Delay && ((TypeMeta.Delay) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            int index = 0;
            for (Map.Entry<String, Expression> e : this.expMap.entrySet()) {
                if (index > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendLiteral(StringType.INSTANCE, e.getKey());
                sqlBuilder.append(_Constant.SPACE_COMMA);
                ((ArmyExpression) e.getValue()).appendSql(context);
                index++;
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            int index = 0;
            for (Map.Entry<String, Expression> e : this.expMap.entrySet()) {
                if (index > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                sqlBuilder.append(_Constant.SPACE)
                        .append(e.getKey())
                        .append(_Constant.SPACE_COMMA)
                        .append(e.getValue());
                index++;
            }

            return sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();

        }

    }//JsonMapFunc


    private static final class NamedComplexArgFunc extends ComplexArgFuncExpression implements NamedExpression {

        private final String expAlias;

        private NamedComplexArgFunc(String name, List<?> argList, TypeMeta returnType, String expAlias) {
            super(name, argList, returnType);
            this.expAlias = expAlias;
        }


        @Override
        public String selectionName() {
            return this.expAlias;
        }


    }//NamedComplexArgFunc


    private static final class CaseFunction extends OperationExpression.SqlFunctionExpression
            implements SQLFunction._CaseWhenSpec,
            SQLFunction._CaseFuncWhenClause,
            SQLFunction._StaticCaseThenClause,
            SQLFunction._CaseElseClause,
            CriteriaContextSpec,
            CaseWhens,
            SQLFunction._DynamicCaseThenClause {

        private final ArmyExpression caseValue;

        private final CriteriaContext context;

        private List<_Pair<ArmyExpression, ArmyExpression>> expPairList;

        private ArmyExpression whenExpression;

        private ArmyExpression elseExpression;

        private MappingType returnType = StringType.INSTANCE;

        private CaseFunction(@Nullable ArmyExpression caseValue) {
            this.caseValue = caseValue;
            this.context = ContextStack.peek();
        }

        @Override
        public CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeMeta.Delay && ((TypeMeta.Delay) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            return this.returnType.mappingType();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" CASE");

            final ArmyExpression caseValue = this.caseValue;
            if (caseValue != null) {
                caseValue.appendSql(context);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                sqlBuilder.append(" WHEN");
                pair.first.appendSql(context);
                sqlBuilder.append(" THEN");
                pair.second.appendSql(context);

            }

            final ArmyExpression elseExpression = this.elseExpression;
            if (elseExpression != null) {
                sqlBuilder.append(" ELSE");
                elseExpression.appendSql(context);
            }

            sqlBuilder.append(" END");

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                return super.toString();
            }
            builder.append(" CASE");

            final ArmyExpression caseValue = this.caseValue;
            if (caseValue != null) {
                builder.append(caseValue);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                builder.append(" WHEN")
                        .append(pair.first)
                        .append(" THEN")
                        .append(pair.second);

            }

            final ArmyExpression elseExpression = this.elseExpression;
            if (elseExpression != null) {
                builder.append(" ELSE")
                        .append(elseExpression);
            }
            return builder.append(" END")
                    .toString();
        }

        @Override
        public CaseFunction when(final @Nullable Expression expression) {
            if (this.whenExpression != null) {
                throw ContextStack.criteriaError(this.context, "last when clause not end.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.whenExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public CaseFunction when(Supplier<Expression> supplier) {
            return this.when(supplier.get());
        }


        @Override
        public CaseFunction when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate) {
            return this.when(valueOperator.apply(predicate));
        }

        @Override
        public CaseFunction when(Function<Expression, Expression> valueOperator, Expression expression) {
            return this.when(valueOperator.apply(expression));
        }

        @Override
        public CaseFunction when(Function<Object, Expression> valueOperator, Object value) {
            return this.when(valueOperator.apply(value));
        }

        @Override
        public <T> CaseFunction when(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return this.when(valueOperator.apply(getter.get()));
        }

        @Override
        public CaseFunction when(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                 String keyName) {
            return this.when(valueOperator.apply(function.apply(keyName)));
        }


        @Override
        public CaseFunction when(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                 BiFunction<Expression, Expression, Expression> valueOperator,
                                 Expression expression) {
            return this.when(expOperator.apply(valueOperator, expression));
        }

        @Override
        public CaseFunction when(ExpressionOperator<Expression, Object, Expression> expOperator,
                                 BiFunction<Expression, Object, Expression> valueOperator, Object value) {
            return this.when(expOperator.apply(valueOperator, value));
        }

        @Override
        public <T> CaseFunction when(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            return this.when(expOperator.apply(valueOperator, getter.get()));
        }

        @Override
        public CaseFunction when(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.when(expOperator.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public CaseFunction when(BetweenValueOperator<Object> expOperator,
                                 BiFunction<Expression, Object, Expression> operator, Object firstValue,
                                 SQLsSyntax.WordAnd and, Object secondValue) {
            return this.when(expOperator.apply(operator, firstValue, and, secondValue));
        }

        @Override
        public <T> CaseFunction when(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter
                , SQLs.WordAnd and, Supplier<T> secondGetter) {
            return this.when(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
        }

        @Override
        public CaseFunction when(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String firstKey, SQLs.WordAnd and, String secondKey) {
            return this.when(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey)));
        }

        @Override
        public CaseFunction when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and
                , Expression second) {
            return this.when(expOperator.apply(first, and, second));
        }

        @Override
        public SQLFunction._CaseElseClause whens(Consumer<CaseWhens> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public CaseFunction ifWhen(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.when(expression);
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.when(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.when(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.when(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.when(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
                , Supplier<T> secondGetter) {
            final T first, second;
            if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
                this.when(expOperator.apply(operator, first, and, second));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String firstKey, SQLs.WordAnd and, String secondKey) {
            final Object first, second;
            if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
                this.when(expOperator.apply(operator, first, and, second));
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(UnaryOperator<IPredicate> predicateOperator
                , BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
                , Supplier<T> firstGetter, SQLsSyntax.WordAnd and, Supplier<T> secondGetter) {
            final T first, second;
            if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
                this.when(predicateOperator.apply(expOperator.apply(operator, first, and, second)));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(UnaryOperator<IPredicate> predicateOperator
                , BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator
                , Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
            final Object first, second;
            if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
                this.when(predicateOperator.apply(expOperator.apply(operator, first, and, second)));
            }
            return this;
        }

        @Override
        public CaseFunction then(final @Nullable Expression expression) {
            final ArmyExpression whenExpression = this.whenExpression;
            if (whenExpression != null) {
                if (expression == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.whenExpression = null; //clear for next when clause
                List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
                if (expPairList == null) {
                    expPairList = new ArrayList<>();
                    this.expPairList = expPairList;
                } else if (!(expPairList instanceof ArrayList)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                expPairList.add(_Pair.create(whenExpression, (ArmyExpression) expression));
            }
            return this;
        }

        @Override
        public CaseFunction then(Supplier<Expression> supplier) {
            if (this.whenExpression != null) {
                this.then(supplier.get());
            }
            return this;
        }

        @Override
        public CaseFunction then(Function<Expression, Expression> valueOperator, Expression expression) {
            if (this.whenExpression != null) {
                this.then(valueOperator.apply(expression));
            }
            return this;
        }

        @Override
        public CaseFunction then(Function<Object, Expression> valueOperator, @Nullable Object value) {
            if (this.whenExpression != null) {
                this.then(valueOperator.apply(value));
            }
            return this;
        }

        @Override
        public <T> CaseFunction then(Function<T, Expression> valueOperator, Supplier<T> getter) {
            if (this.whenExpression != null) {
                this.then(valueOperator.apply(getter.get()));
            }
            return this;
        }

        @Override
        public CaseFunction then(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            if (this.whenExpression != null) {
                this.then(valueOperator.apply(function.apply(keyName)));
            }
            return this;
        }

        @Override
        public CaseFunction then(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                 BiFunction<Expression, Expression, Expression> valueOperator,
                                 Expression expression) {
            if (this.whenExpression != null) {
                this.then(expOperator.apply(valueOperator, expression));
            }
            return this;
        }

        @Override
        public CaseFunction then(ExpressionOperator<Expression, Object, Expression> expOperator,
                                 BiFunction<Expression, Object, Expression> valueOperator, Object value) {
            if (this.whenExpression != null) {
                this.then(expOperator.apply(valueOperator, value));
            }
            return this;
        }

        @Override
        public <T> CaseFunction then(ExpressionOperator<Expression, T, Expression> expOperator,
                                     BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            if (this.whenExpression != null) {
                this.then(expOperator.apply(valueOperator, getter.get()));
            }
            return this;
        }

        @Override
        public CaseFunction then(ExpressionOperator<Expression, Object, Expression> expOperator,
                                 BiFunction<Expression, Object, Expression> valueOperator,
                                 Function<String, ?> function, String keyName) {
            if (this.whenExpression != null) {
                this.then(expOperator.apply(valueOperator, function.apply(keyName)));
            }
            return this;
        }


        @Override
        public _CaseEndClause elseValue(final @Nullable Expression expression) {
            if (this.expPairList == null) {
                throw noWhenClause();
            } else if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            } else if (this.elseExpression != null) {
                throw ContextStack.criteriaError(this.context, "duplicate else clause.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.elseExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public _CaseEndClause elseValue(Supplier<Expression> supplier) {
            return this.elseValue(supplier.get());
        }


        @Override
        public _CaseEndClause elseValue(Function<Expression, Expression> valueOperator, Expression expression) {
            return this.elseValue(valueOperator.apply(expression));
        }

        @Override
        public _CaseEndClause elseValue(Function<Object, Expression> valueOperator, @Nullable Object value) {
            return this.elseValue(valueOperator.apply(value));
        }

        @Override
        public <T> _CaseEndClause elseValue(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return this.elseValue(valueOperator.apply(getter.get()));
        }

        @Override
        public _CaseEndClause elseValue(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return this.elseValue(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public _CaseEndClause elseValue(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                        BiFunction<Expression, Expression, Expression> valueOperator,
                                        Expression expression) {
            return this.elseValue(expOperator.apply(valueOperator, expression));
        }

        @Override
        public _CaseEndClause elseValue(ExpressionOperator<Expression, Object, Expression> expOperator,
                                        BiFunction<Expression, Object, Expression> valueOperator, Object value) {
            return this.elseValue(expOperator.apply(valueOperator, value));
        }

        @Override
        public <T> _CaseEndClause elseValue(ExpressionOperator<Expression, T, Expression> expOperator,
                                            BiFunction<Expression, T, Expression> valueOperator,
                                            Supplier<T> getter) {
            return this.elseValue(expOperator.apply(valueOperator, getter.get()));
        }

        @Override
        public _CaseEndClause elseValue(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.elseValue(expOperator.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public _CaseEndClause ifElse(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.elseValue(expression);
            }
            return this;
        }

        @Override
        public <T> _CaseEndClause ifElse(Function<T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.elseValue(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public _CaseEndClause ifElse(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.elseValue(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public <T> _CaseEndClause ifElse(ExpressionOperator<Expression, T, Expression> expOperator,
                                         BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.elseValue(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public _CaseEndClause ifElse(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.elseValue(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public SimpleExpression end() {
            this.endCaseFunction();
            return this;
        }


        @Override
        public SimpleExpression end(final @Nullable TypeInfer type) {

            this.endCaseFunction();

            if (type == null) {
                throw ContextStack.nullPointer(this.context);
            }
            if (type instanceof MappingType) {
                this.returnType = (MappingType) type;
            } else {
                this.returnType = type.typeMeta().mappingType();
            }
            return this;
        }

        private void endCaseFunction() {
            if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            }
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null) {
                throw noWhenClause();
            } else if (expPairList instanceof ArrayList) {
                this.expPairList = _CollectionUtils.unmodifiableList(expPairList);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
        }

        private CriteriaException noWhenClause() {
            return ContextStack.criteriaError(this.context, "Not found any when clause.");
        }

        private CriteriaException lastWhenClauseNotEnd() {
            return ContextStack.criteriaError(this.context, "current when clause not end");
        }


    }//CaseFunc


    private static final class GlobalWindow implements ArmyWindow {

        private static final GlobalWindow INSTANCE = new GlobalWindow();

        private GlobalWindow() {
        }

        @Override
        public void appendSql(final _SqlContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArmyWindow endWindowClause() {
            return this;
        }

        @Override
        public String windowName() {
            throw new IllegalStateException("this is global window");
        }

        @Override
        public void prepared() {
            //no-op
        }

        @Override
        public void clear() {
            //no-op
        }

    }//GlobalWindow


}
